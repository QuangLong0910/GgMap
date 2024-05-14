package developer.kotlin.gpseagletech

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

@RequiresApi(api = Build.VERSION_CODES.N)
class MainActivity : AppCompatActivity() {
    private lateinit var locationCallback: LocationCallback

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var gps: TextView
    private lateinit var gps1: TextView
    private lateinit var link: TextView
    private val TAG = "zzzz"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        gps = findViewById(R.id.gps)
        gps1 = findViewById(R.id.gps1)
        link = findViewById(R.id.link)
        link.text = "http://www.google.com/maps/search/?api=1&query="
    }

    private val locationPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            val fineLocationGranted = result[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
            val coarseLocationGranted = result[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
            if (fineLocationGranted) {
                // Precise location access granted.
                Log.d(TAG, "Đã cấp quyền vị trí chính xác tuyệt đối")
            } else if (coarseLocationGranted) {
                // Only approximate location access granted.
                Log.d(TAG, "Đã cấp quyền vị trí chính xác tương đối")
            } else {
                // No location access granted.
                Log.d(TAG, "Bị từ chối cấp quyền vị trí")
            }
        }

    override fun onResume() {
        super.onResume()
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionRequest.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
            return
        }

        // Tạo LocationCallback
        locationCallback = object : LocationCallback() {
          override fun onLocationResult(locationResult: LocationResult) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    // Xử lý vị trí mới ở đây
                    Log.d(TAG, "New Location: Lat = ${location.latitude}, Lng = ${location.longitude}")
                    gps.text = location.latitude.toString()
                    gps1.text = location.longitude.toString()
                }
            }
        }

        // Yêu cầu cập nhật vị trí
        val locationRequest: LocationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 1000 // Độ tần suất cập nhật vị trí, ở đây là 10 giây
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

}
