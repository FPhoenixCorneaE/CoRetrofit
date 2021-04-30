package com.fphoenixcorneae.rxretrofit.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.fphoenixcorneae.rxretrofit.demo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ActivityMainBinding.inflate(layoutInflater).rootView)
    }
}