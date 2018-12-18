package com.zerotoonelabs.android

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.extensions.getColorCompat
import com.zerotoonelabs.android.ui.main.MainFragment
import kotlinx.android.synthetic.main.main_activity.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }
        text.setBackgroundColor(getColorCompat(R.color.colorAccent))


    }

}
