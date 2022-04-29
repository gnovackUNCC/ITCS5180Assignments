package com.gnovack.group30_inclass06

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.gnovack.group30_inclass06.databinding.ActivityMainBinding
import java.util.concurrent.Executors

//Inclass06
//MainActivity
//Gabriel Novack

class MainActivity : AppCompatActivity() {
    lateinit var binding:ActivityMainBinding

    val PROG = 1
    val FINAL = 2
    val STATUS_START = 99

    var numCalc = 1
    var activeNumCalc = numCalc

    var curSum = 0.0

    val numbers:MutableList<Double> = mutableListOf()

    var canClick = true

    val handler:Handler = Handler(Looper.myLooper()!!, Handler.Callback {msg ->
        if (msg.what == PROG){
            updateProg((msg.obj as Pair<Int, Double>).first)
            val newNum = (msg.obj as Pair<Int, Double>).second
            numbers.add(newNum)
            curSum += newNum
            updateList()
        } else if(msg.what == STATUS_START) {
            canClick = false
        }else {
            canClick = true
        }
        true
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root

        val taskPool = Executors.newFixedThreadPool(2)

        setContentView(view)

        with(binding.numList){
            layoutManager = LinearLayoutManager(context)
            adapter = NumberListAdapter(numbers)
        }

        binding.complexSlider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                numCalc = p1
                if(p1 > 1)
                    binding.numTimes.text = resources.getString(R.string.times, p1)
                else
                    binding.numTimes.text = resources.getString(R.string._1_time)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                Log.d("TAG", "onStartTrackingTouch: ")
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                Log.d("TAG", "onStopTrackingTouch: ")
            }

        })

        binding.genButton.setOnClickListener {
            if(canClick) {
                binding.progressBar.visibility = View.VISIBLE
                activeNumCalc = numCalc
                updateProg(0)
                taskPool.execute(object : Runnable {
                    override fun run() {
                        val startMessage = Message()
                        startMessage.what = STATUS_START
                        handler.sendMessage(startMessage)
                        for (i in 1..activeNumCalc) {
                            val progMessage = Message()
                            progMessage.what = PROG
                            progMessage.obj = Pair(i, HeavyWork.getNumber())
                            handler.sendMessage(progMessage)
                        }
                        val finalMessage = Message()
                        finalMessage.what = FINAL
                        handler.sendMessage(finalMessage)
                    }
                })
            } else {
                Toast.makeText(this, getString(R.string.error), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateProg(curProg:Int){
        binding.progText.text = resources.getString(R.string.progress, curProg, activeNumCalc)
        binding.progressBar.progress = ((curProg / activeNumCalc.toDouble()) * 100).toInt()
    }
    private fun updateList(){
        binding.average.text = resources.getString(R.string.average, curSum/numbers.size)
        with(binding.numList){
            adapter = NumberListAdapter(numbers)
        }
    }

}