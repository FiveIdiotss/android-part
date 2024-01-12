    package com.minhoi.memento.ui.join

    import androidx.appcompat.app.AppCompatActivity
    import android.os.Bundle
    import androidx.navigation.NavController
    import androidx.navigation.findNavController
    import com.minhoi.memento.R

    class JoinActivity : AppCompatActivity() {

        private lateinit var navController: NavController
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_join)
        }

        override fun onSupportNavigateUp(): Boolean {
            navController = findNavController(R.id.fragmentContainerView)
            return navController.navigateUp() || super.onSupportNavigateUp()
        }
    }