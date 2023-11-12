package com.example.weatherforacastapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.example.weatherforacastapp.constant.Const.Companion.permissions
import com.example.weatherforacastapp.model.MyLatLng
import com.example.weatherforacastapp.ui.theme.WeatherForacastAppTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.coroutineScope

class MainActivity : ComponentActivity() {
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var locationRequired: Boolean = false

    override fun onResume() {
        super.onResume()
        if (locationRequired) startLocationUpdate();
    }

    override fun onPause() {
        super.onPause()
        locationCallback?.let {
            fusedLocationProviderClient?.removeLocationUpdates(it)
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdate() {
        locationCallback?.let {
            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY, 100
            )
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(3000)
                .setMaxUpdateDelayMillis(100)
                .build()

            fusedLocationProviderClient?.requestLocationUpdates(
                locationRequest,
                it,
                Looper.getMainLooper()
            )
        }
     }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initLocationClient()
        setContent {

            //This will keep the value of our current location
            var currentLocation by remember {
                mutableStateOf(MyLatLng(0.0,0.0))
            }

            //implement location call back
            locationCallback = object: LocationCallback() {
                override fun onLocationResult(p0: LocationResult) {
                    super.onLocationResult(p0)
                    for (location in p0.locations) {
                        currentLocation = MyLatLng(
                            location.latitude,
                            location.longitude
                        )
                    }
                }
            }

            WeatherForacastAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    LocationScreen(this@MainActivity, currentLocation)
                  }
            }
        }
    }

    @Composable
    private fun LocationScreen(context: Context, currentLocation: MyLatLng) {
        //request runtime permission
        val launcherMultiplePermissions = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissionMap ->
            val areGranted = permissionMap.values.reduce { accepted, next ->
                accepted && next
            }
            //check all permissions are accepted
            if (areGranted) {
                locationRequired = true;
                startLocationUpdate();
                Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
            val systemUiController = rememberSystemUiController()
            
            DisposableEffect(key1 = true, effect = {
                systemUiController.isSystemBarsVisible = false //Hide status bar
                onDispose {
                    systemUiController.isSystemBarsVisible = true //show status bar
                }
            })
            
            LaunchedEffect(key1 = currentLocation, block = {
                coroutineScope {
                    if (permissions.all {
                            ContextCompat.checkSelfPermission(
                                context,
                                it
                            ) == PackageManager.PERMISSION_GRANTED
                        }) {
                        //if all permissions granted
                        startLocationUpdate()
                    } else {
                        launcherMultiplePermissions.launch(permissions)
                    }
                }
            })

        Text(text = "${currentLocation.lat}/${currentLocation.lng}")
    }

    private fun initLocationClient() {
         fusedLocationProviderClient = LocationServices
             .getFusedLocationProviderClient(this)
    }
}

