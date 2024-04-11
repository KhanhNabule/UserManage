package com.luv2code.springboot.cruddemo.controller;

import com.luv2code.springboot.cruddemo.dao.EmployeeRepository;
import com.luv2code.springboot.cruddemo.entity.Employee;
import com.luv2code.springboot.cruddemo.service.EmployeeService;
import com.luv2code.springboot.cruddemo.dto.EmployeeDto;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired
    private EmployeeRepository repository;
    @Autowired
    private EmployeeService service;
    @GetMapping("/list")
    public String getEmployee(Model model, @Param("keyword") String keyword) {
        List<Employee> employeeList = service.listAll(keyword);
        model.addAttribute("employees", employeeList);
        return "employees/list-employees";
    }
    @GetMapping("/showFormForAdd")
    public String showFormEmployee(Model model) {
        EmployeeDto employeeDto = new EmployeeDto();
        model.addAttribute("employees", employeeDto);
        return "employees/employee-form";
    }
   
    @PostMapping("/showFormForAdd")
    public String addEmployee(@Valid @ModelAttribute("employees") EmployeeDto employeeDto, BindingResult result) {
        if(employeeDto.getImage().isEmpty()) {
            result.addError(new FieldError("employeeDto", "image", "The image file is required"));
        }

        if(result.hasErrors()) {
            return "employees/employee-form";
        }
        MultipartFile image = employeeDto.getImage();
        Date createdAt = new Date();
        String storageFileName = createdAt.getTime() + "_" + image.getOriginalFilename();

        try {
            String uploadDir = "public/images/";
            Path uploadPath = Paths.get(uploadDir);

            if(!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            try (InputStream inputStream = image.getInputStream()) {
                Files.copy(inputStream, Paths.get(uploadDir + storageFileName), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
        }

        Employee employee = new Employee();
        employee.setFirstName(employeeDto.getFirstName());
        employee.setLastName(employeeDto.getLastName());
        employee.setEmail(employeeDto.getEmail());
        employee.setCreatedAt(createdAt);
        employee.setImage(storageFileName);

        repository.save(employee);
        return "redirect:/employees/list";
    }
    @GetMapping("/showFormForUpdate")
    public String showFormUpdateEmployee(@RequestParam int id, Model model) {
       try {
           Employee employee = repository.findById(id).get();
           model.addAttribute("employee", employee);

           EmployeeDto employeeDto = new EmployeeDto();
           employeeDto.setFirstName(employee.getFirstName());
           employeeDto.setLastName(employee.getLastName());
           employeeDto.setEmail(employee.getEmail());

           model.addAttribute("employeeDto", employeeDto);
       } catch(Exception ex) {
           System.out.println("Exception: " + ex.getMessage());
           return "redirect:/employees/list";
       }
        return "employees/employee-form-update";
    }
    
    @PostMapping("/showFormForUpdate")
    public String updateEmployee (@RequestParam int id, Model model,
                                  @Valid @ModelAttribute EmployeeDto employeeDto, BindingResult result) {
        try {
            Employee employee = repository.findById(id).get();
            model.addAttribute("employee", employee);

            if(result.hasErrors()) {
                return "employees/employee-form-update";
            }

            if(!employeeDto.getImage().isEmpty()) {
                //delete old image
                String uploadDir = "public/images/";
                Path oldImagePath = Paths.get(uploadDir + employee.getImage());
                try {
                    Files.delete(oldImagePath);
                } catch (Exception ex) {
                    System.out.println("Exception: " + ex.getMessage());
                }

                //save new image file
                MultipartFile image = employeeDto.getImage();
                Date createdAt = new Date();
                String storageFileName = createdAt.getTime() + "_" + image.getOriginalFilename();

                try (InputStream inputStream = image.getInputStream()) {
                    Files.copy(inputStream, Paths.get(uploadDir + storageFileName),
                            StandardCopyOption.REPLACE_EXISTING);
                }

                employee.setImage(storageFileName);
            }
            employee.setFirstName(employeeDto.getFirstName());
            employee.setLastName(employeeDto.getLastName());
            employee.setEmail(employeeDto.getEmail());
            repository.save(employee);
        } catch(Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
        }
        return "redirect:/employees/list";
    }

    @GetMapping("/delete")
    public String deleteEmployee(@RequestParam int id) {
        try {
            Employee employee = repository.findById(id).get();
            Path imagePath = Paths.get("public/images" + employee.getImage());

            try {
                Files.delete(imagePath);
            } catch (Exception ex) {
                System.out.println("Exception: " + ex.getMessage());
            }
            repository.delete(employee);
        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
        }
        return "redirect:/employees/list";
    }
   
}