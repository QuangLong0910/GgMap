package developer.kotlin.gpseagletech

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.PopupMenu
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var locationCallback: LocationCallback

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var mGoogleMap: GoogleMap? = null

    @SuppressLint("MissingInflatedId", "SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val mapOptionButton: ImageButton = findViewById(R.id.menuMap)
        val popupMenu = PopupMenu(this, mapOptionButton)
        popupMenu.menuInflater.inflate(R.menu.map_options, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            changeMap(menuItem.itemId)
            true
        }
    }

    private fun changeMap(itemId: Int) {
        when (itemId) {
            R.id.normal_map -> mGoogleMap?.mapType = GoogleMap.MAP_TYPE_NORMAL
            R.id.hybrid_map -> mGoogleMap?.mapType = GoogleMap.MAP_TYPE_HYBRID
            R.id.satellite_map -> mGoogleMap?.mapType = GoogleMap.MAP_TYPE_SATELLITE
            R.id.terrain_map -> mGoogleMap?.mapType = GoogleMap.MAP_TYPE_TERRAIN
        }

    }

    override fun onMapReady(p0: GoogleMap) {

        mGoogleMap = p0


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
                    val sydney = LatLng(location.latitude, location.longitude)
                    mGoogleMap!!.moveCamera(CameraUpdateFactory.newLatLng(sydney))
                    val options = MarkerOptions().position(sydney).title("Sydney")
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    mGoogleMap!!.addMarker(options)

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
