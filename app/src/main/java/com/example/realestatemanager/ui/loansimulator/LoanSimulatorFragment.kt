package com.example.realestatemanager.ui.loansimulator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.realestatemanager.R
import com.example.realestatemanager.databinding.FragmentLoanSimulatorBinding
import kotlin.math.pow

class LoanSimulatorFragment : Fragment() {
    private val binding: FragmentLoanSimulatorBinding by lazy {
        FragmentLoanSimulatorBinding.inflate(
            layoutInflater
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        binding.simulateLoanButton.setOnClickListener {
            val loanAmount = binding.loanAmount.text.toString().toDouble()
            val annualInterestRate = binding.annualInterestRate.text.toString().toDouble()
            val loanTermMonths = binding.loanTermMonths.text.toString().toInt()
            val (monthlyPayment, totalCost) = simulateMortgageLoan(
                loanAmount,
                annualInterestRate,
                loanTermMonths
            )
            binding.simulateLoanResult.text = getString(
                R.string.loan_simulator_result,
                String.format("%.2f",monthlyPayment),
                String.format("%.2f",totalCost)
            )
        }
        return binding.root
    }

    fun simulateMortgageLoan(
        loanAmount: Double,
        annualInterestRate: Double,
        loanTermMonths: Int
    ): Pair<Double, Double> {
        val monthlyInterestRate = annualInterestRate / 12 / 100
        val monthlyPayment =
            loanAmount * (monthlyInterestRate * (1 + monthlyInterestRate).pow(loanTermMonths.toDouble())) /
                    ((1 + monthlyInterestRate).pow(loanTermMonths.toDouble()) - 1)
        val totalCost = monthlyPayment * loanTermMonths

        return Pair(monthlyPayment, totalCost)
    }

}