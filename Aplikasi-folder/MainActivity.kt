package com.example.iot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.compose.runtime.livedata.observeAsState
import com.example.iot.ui.theme.IoTTheme
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.example.iot.ui.screens.HomeScreen


class MainActivity : ComponentActivity() {
    private val _pH = MutableLiveData<Double>()
    var pH: LiveData<Double> = _pH

    private val _voltage = MutableLiveData<Double>()
    val voltage: LiveData<Double> = _voltage

    private val _aerator = MutableLiveData<Boolean>()
    val aerator: LiveData<Boolean> = _aerator

    private val _auto = MutableLiveData<Boolean>()
    val auto: LiveData<Boolean> = _auto

    private val _temperature = MutableLiveData<Double>()
    val temperature: LiveData<Double> = _temperature

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val database = FirebaseDatabase.getInstance().reference.child("Sensor")
        fetchDataFromFirebase()
        setContent {
            IoTTheme {
                val pHValue by pH.observeAsState()
                val voltageValue by voltage.observeAsState()
                val aeratorValue by aerator.observeAsState(initial = false)
                val autoValue by auto.observeAsState(initial = false)
                val tempValue by temperature.observeAsState()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HomeScreen(
                        pH = pHValue,
                        voltage = voltageValue,
                        aeratorOn = aeratorValue,
                        isAuto = autoValue,
                        temperature = tempValue,
                        onToggleAerator = {
                            database.child("AERATOR").setValue(!aeratorValue)
                        },
                        onToggleAuto = {
                            database.child("AUTO").setValue(!autoValue)
                        }
                    )
                }
            }
        }
    }

    private fun fetchDataFromFirebase(){
        val database = FirebaseDatabase.getInstance().reference.child("Sensor")
        database.child("FARM_VOL").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _pH.value = snapshot.getValue(Double::class.java)
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        database.child("LSET_VOL").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _voltage.value = snapshot.getValue(Double::class.java)
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        database.child("AERATOR").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _aerator.value = snapshot.getValue(Boolean::class.java) ?: false
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        database.child("AUTO").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _auto.value = snapshot.getValue(Boolean::class.java) ?: false
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        database.child("Temperature").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _temperature.value = snapshot.getValue(Double::class.java)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}

