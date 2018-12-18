package com.zerotoonelabs.android.ui.main

import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.zerotoonelabs.pickerview.popwindow.DatePickerPopWin
import com.zerotoonelabs.android.R

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.main_fragment, container, false)

        val btnPicker = root.findViewById<Button>(R.id.picker)

        btnPicker.setOnClickListener {
            val pickerPopWin =
                DatePickerPopWin.Builder(activity,
                    DatePickerPopWin.OnDatePickedListener { year, month, day, dateDesc ->
                        Toast.makeText(activity, dateDesc, Toast.LENGTH_SHORT).show()
                    }).textConfirm("CONFIRM") //text of confirm button
                    .textCancel("CANCEL") //text of cancel button
                    .btnTextSize(16) // button text size
                    .viewTextSize(25) // pick view text size
                    .colorCancel(Color.parseColor("#999999")) //color of cancel button
                    .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                    .minDate(1540549260000L) //min year in loop
                    .maxDate(1541240460000L) // max year in loop
                    .dateChose("2013-11-11") // date chose when init popwindow
                    .build()
            pickerPopWin.showPopWin(activity)
        }

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
