
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

    // CREATE
    public Payroll savePayroll(Payroll payroll) {
        return payrollRepo.save(payroll);
    }

    // READ ALL
    public List<Payroll> getAllPayroll() {
        return payrollRepo.findAll();
    }

    // READ BY ID
    public Payroll getPayrollById(Long id) {
        return payrollRepo.findById(id).orElse(null);
    }

    // UPDATE
    public Payroll updatePayroll(Payroll payroll, Long id) {

        Payroll existing =
                payrollRepo.findById(id).orElse(null);

        if (existing != null) {
            existing.setEmployeeId(payroll.getEmployeeId());
            existing.setPayMonth(payroll.getPayMonth());
            existing.setBasicSalary(payroll.getBasicSalary());
            existing.setHra(payroll.getHra());
            existing.setAllowance(payroll.getAllowance());
            existing.setPf(payroll.getPf());
            existing.setTax(payroll.getTax());
            existing.setDeduction(payroll.getDeduction());
            existing.setNetSalary(payroll.getNetSalary());

            return payrollRepo.save(existing);
        }

        return null;
    }

    // DELETE
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
