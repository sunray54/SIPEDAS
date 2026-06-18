package ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sipedas.app.databinding.ActivitySubscriptionBinding

class SubscriptionActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySubscriptionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubscriptionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSubscribeMonthly.setOnClickListener {
            // Panggil PaymentManager
            PaymentManager.startPayment(this, "monthly")
        }

        binding.btnSubscribeYearly.setOnClickListener {
            PaymentManager.startPayment(this, "yearly")
        }
    }
}