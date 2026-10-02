package com.employee360.backend.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.employee360.backend.entities.Payroll;
import com.employee360.backend.repos.PayrollRepo;

@Service
public class PayrollSer {

    @Autowired
    private PayrollRepo payrollRepo;

    // Save Payroll
    public Payroll savePayroll(Payroll payroll) {

        return payrollRepo.save(payroll);
    }

    // Get All Payroll
    public List<Payroll> getAllPayroll() {

        return payrollRepo.findAll();
    }

    // Get Payroll By ID
    public Payroll getPayrollById(Long id) {

        return payrollRepo.findById(id).orElse(null);
    }

    // Update Payroll
    public Payroll updatePayroll(Payroll payroll, Long id) {

        Payroll existingPayroll =
                payrollRepo.findById(id).orElse(null);

        if (existingPayroll != null) {

            existingPayroll.setEmployeeId(
                    payroll.getEmployeeId());

            existingPayroll.setPayMonth(
                    payroll.getPayMonth());

            existingPayroll.setBasicSalary(
                    payroll.getBasicSalary());

            existingPayroll.setHra(
                    payroll.getHra());

            existingPayroll.setAllowance(
                    payroll.getAllowance());

            existingPayroll.setPf(
                    payroll.getPf());

            existingPayroll.setTax(
                    payroll.getTax());

            existingPayroll.setDeduction(
                    payroll.getDeduction());

            existingPayroll.setNetSalary(
                    payroll.getNetSalary());

            return payrollRepo.save(existingPayroll);
        }

        return null;
    }

    // Delete Payroll
    public boolean deletePayroll(Long id) {

        Payroll payroll =
                payrollRepo.findById(id).orElse(null);

        if (payroll != null) {

            payrollRepo.delete(payroll);

            return true;
        }

        return false;
    }

}
