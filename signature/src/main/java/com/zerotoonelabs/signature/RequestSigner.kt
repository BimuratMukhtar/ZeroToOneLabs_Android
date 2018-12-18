package com.zerotoonelabs.signature

import kz.gov.pki.kalkan.asn1.pkcs.PKCSObjectIdentifiers
import kz.gov.pki.kalkan.jce.provider.KalkanProvider
import kz.gov.pki.kalkan.xmldsig.KncaXS
import org.apache.xml.security.encryption.XMLCipherParameters
import org.apache.xml.security.signature.XMLSignature
import org.apache.xml.security.transforms.Transforms
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.io.StringWriter
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.Security
import java.security.cert.X509Certificate
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

class RequestSigner constructor(
    private val inputStream: InputStream,
    private val password: String
) {

    companion object {

        @Volatile
        private var INSTANCE: KeyStore? = null

        fun getInstance(inputStream: InputStream, password: String, provName: String): KeyStore =
        // when user select different input stream, it returns the old one,
        // because the old one is not null!
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: loadKeystore(inputStream, password, provName).also { INSTANCE = it }
            }

        @Throws(KeyStoreException::class, IOException::class)
        private fun loadKeystore(inputStream: InputStream, password: String, provName: String): KeyStore {
//            val keystore = KeyStore.getInstance("PKCS12", provName)
            val keystore = KeyStore.getInstance("PKCS12")
            keystore.load(inputStream, password.toCharArray())
            return keystore
        }

        fun destroyInstance() {
            INSTANCE = null
        }

        private const val MoreAlgorithmsSpecNS = "http://www.w3.org/2001/04/xmldsig-more#"
        private const val SHA256 = "http://www.w3.org/2001/04/xmlenc#sha256"
        private const val CAN_METHOD_EXCLUSIVE = "http://www.w3.org/2001/04/xmlenc#sha256"

        private val TAG_TO_SIGN = "data_go"
        private val TAG_LOCATION_OF_SIGNATURE = "sign_go"
    }

    fun getUserData(): CertificateUserData {
        val kalkanProvider = KalkanProvider()
        Security.addProvider(kalkanProvider)
        KncaXS.loadXMLSecurity()
        val keystore = getInstance(inputStream, password, kalkanProvider.name)
        val aliases = keystore.aliases()
        val alias = aliases.nextElement()
        val certificate = keystore.getCertificate(alias) as X509Certificate
        return CertificateUserData(certificate)
    }

    fun signXmlFile(arrayByte: ByteArray): String? {

        val kalkanProvider = KalkanProvider()
        Security.addProvider(kalkanProvider)
        KncaXS.loadXMLSecurity()

        val keystore = getInstance(inputStream, password, kalkanProvider.name)

        val document = getDocument(arrayByte)
        val signatureLocation = getDestinationTag(document)
        avoidJava7Bug(document)

        //Loading keystores
        val aliases = keystore.aliases()
        var alias: String? = null
        while (aliases.hasMoreElements()) {
            alias = aliases.nextElement()
        }

        val privateKey = keystore.getKey(alias, password.toCharArray())
        val certificate = keystore.getCertificate(alias) as X509Certificate

        val signMethod: String
        val digestMethod: String
        val sigAlgOid = certificate.sigAlgOID

        when (sigAlgOid) {
            PKCSObjectIdentifiers.sha1WithRSAEncryption.id -> {
                signMethod = MoreAlgorithmsSpecNS + "rsa-sha1"
                digestMethod = MoreAlgorithmsSpecNS + "sha1"
            }
            PKCSObjectIdentifiers.sha256WithRSAEncryption.id -> {
                signMethod = MoreAlgorithmsSpecNS + "rsa-sha256"
                digestMethod = SHA256
            }
            else -> {
                signMethod = MoreAlgorithmsSpecNS + "gost34310-gost34311"
                digestMethod = MoreAlgorithmsSpecNS + "gost34311"
            }
        }

        val canMethod = CAN_METHOD_EXCLUSIVE

        val signature = XMLSignature(document, "", signMethod, canMethod)

        if (document.firstChild == null) return null

        signatureLocation.appendChild(signature.element)

        val transforms = Transforms(document)
        transforms.addTransform(XMLCipherParameters.N14C_XML_CMMNTS)

        signature.addDocument("#$TAG_TO_SIGN", transforms, digestMethod)
        signature.addKeyInfo(certificate)
        signature.sign(privateKey)

        val os = StringWriter()
        val tf = TransformerFactory.newInstance()
        val transformer = tf.newTransformer()
        transformer.transform(DOMSource(document), StreamResult(os))
        os.close()
        return os.toString()
    }

    private fun avoidJava7Bug(document: Document) {
        val xpath = XPathFactory.newInstance().newXPath()

        val exprAssertion = xpath.compile("//*[local-name()='$TAG_TO_SIGN']")
        val assertionNode =
            exprAssertion.evaluate(document, XPathConstants.NODE) as Element? ?: throw NoDataTagException()
        // Must mark ID Atrribute as XML ID to avoid BUG in Java 1.7.25.
        if (assertionNode.hasAttribute("id"))
            assertionNode.setIdAttribute("id", true)
        if (assertionNode.hasAttribute("Id"))
            assertionNode.setIdAttribute("Id", true)
        if (assertionNode.hasAttribute("ID"))
            assertionNode.setIdAttribute("ID", true)
    }

    private fun getDestinationTag(document: Document): Node {
        val nodes = document.getElementsByTagName(TAG_LOCATION_OF_SIGNATURE)
        return if (nodes.length == 0) {
            document.firstChild.appendChild(document.createElement(TAG_LOCATION_OF_SIGNATURE))
        } else {
            nodes.item(0)
        }
    }

    private fun getDocument(arrayByte: ByteArray): Document {
        val documentBuilderFactory = DocumentBuilderFactory.newInstance()
        documentBuilderFactory.isNamespaceAware = true
        val documentBuilder = documentBuilderFactory.newDocumentBuilder()
        return documentBuilder.parse(ByteArrayInputStream(arrayByte))
    }
}