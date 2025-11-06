package com.shuxuejia.managementsystem.controller;

import com.shuxuejia.managementsystem.dao.DepartmentDao;
import com.shuxuejia.managementsystem.dao.EmployeeDao;
import com.shuxuejia.managementsystem.pojo.Department;
import com.shuxuejia.managementsystem.pojo.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collection;

/*
   @auth0r  chagumu
    
*/
@Controller
public class EmployeeController {
    @Autowired
    private EmployeeDao employeeDao;
    @Autowired
    private DepartmentDao departmentDao;

    @RequestMapping("/emps")
    public String list(Model model) {

            Collection<Employee> emps = employeeDao.getAllEmployees();
            model.addAttribute("emps", emps);  // 移除了多余的参数名
            return "emp/tables";
    }

    @GetMapping("/add")
    public String add(Model model) {
        //查出所有的部门信息,添加到departments中,用于前端接收
        Collection<Department> departments = departmentDao.departments();
        model.addAttribute("departments", departments);
        return "emp/add";//返回到添加员工页面
    }

    @PostMapping("/add")
    public String addEmp(Employee employee, Model model) {
        String errorMsg = null;

        // 校验姓名非空
        if (employee.getLastname() == null ) {
            errorMsg = "姓名不能为空";
        }
        // 校验邮箱非空
        else if (employee.getEmail() == null || employee.getEmail().trim().isEmpty()) {
            errorMsg = "邮箱不能为空";
        }
        // 校验部门非空（通过department.id判断）
        else if (employee.getDepartment() == null || employee.getDepartment().getId() == null) {
            errorMsg = "部门不能为空";
        }

        // 若有错误，返回添加页面并携带错误信息
        if (errorMsg != null) {
            model.addAttribute("msg", errorMsg);
            // 同时需要把部门列表再传给前端（否则下拉框会为空）
            Collection<Department> departments = departmentDao.departments();
            model.addAttribute("departments", departments);
            return "emp/add";
        }

        // 字段校验通过，执行添加操作
        employeeDao.addEmployee(employee);
        return "redirect:/emps";
    }


    //restful风格接收参数
    @RequestMapping("/edit/{id}")
    public String edit(@PathVariable("id") int id, Model model) {
        //查询指定id的员工,添加到empByID中,用于前端接收
        Employee employeeByID = employeeDao.getEmployeeByID(id);
        model.addAttribute("empByID", employeeByID);
        //查出所有的部门信息,添加到departments中,用于前端接收
        Collection<Department> departments = departmentDao.departments();
        model.addAttribute("departments", departments);
        return "/emp/edit";//返回到编辑员工页面
    }

    @PostMapping("/edit")
    public String EditEmp(Employee employee) {
        employeeDao.addEmployee(employee);//添加一个员工
        return "redirect:/emps";//添加完成重定向到/emps,刷新列表
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Integer id) {
        employeeDao.deleteEmployeeByID(id);
        return "redirect:/emps";
    }

}
