package com.spidersam.michauchera

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.spidersam.michauchera.utils.GestionadorPresupuestosWorker
import com.spidersam.michauchera.utils.WorkManagerUtil

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.navController

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNav.setupWithNavController(navController)

        WorkManagerUtil.configurarPresupuestoWorker(this)
        GestionadorPresupuestosWorker.programarMonitoreoPeriodico(applicationContext)
    }
}
