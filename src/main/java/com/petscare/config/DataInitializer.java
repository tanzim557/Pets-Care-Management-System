package com.petscare.config;

import com.petscare.entity.*;
import com.petscare.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private RescueTeamRepository rescueTeamRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private AdoptionPetRepository adoptionPetRepository;

    @Override
    public void run(String... args) {
        try {
            if (adminRepository.count() == 0) {
                log.info("No admin found in database. Initializing default cloud data...");

                // 1. Create Default Admin
                Admin admin = new Admin();
                admin.setUsername("admin");
                admin.setPassword("admin123");
                admin.setFirstName("Super");
                admin.setLastName("Admin");
                admin.setPhone("01711122233");
                admin.setGender("M");
                admin.setAge(35);
                Admin savedAdmin = adminRepository.save(admin);

                // 2. Create Default Doctors (Employees)
                Employee doctor1 = new Employee();
                doctor1.setAdminId(savedAdmin.getId());
                doctor1.setFirstName("Rupa");
                doctor1.setLastName("Doctor");
                doctor1.setUsername("rupa");
                doctor1.setPassword("1234");
                doctor1.setRole("Doctor");
                doctor1.setSpecialty("Surgeon");
                doctor1.setPhone("01911122233");
                doctor1.setGender("F");
                doctor1.setAge(28);
                doctor1.setSalary(50000);
                doctor1.setWorkingHours(8);
                doctor1.setLocation("Dhaka");
                employeeRepository.save(doctor1);

                Employee doctor2 = new Employee();
                doctor2.setAdminId(savedAdmin.getId());
                doctor2.setFirstName("Farzana");
                doctor2.setLastName("Chowdhury");
                doctor2.setUsername("emp1");
                doctor2.setPassword("pass123");
                doctor2.setRole("Doctor");
                doctor2.setSpecialty("Dermatologist");
                doctor2.setPhone("01911100001");
                doctor2.setGender("F");
                doctor2.setAge(27);
                doctor2.setSalary(45000);
                doctor2.setWorkingHours(8);
                doctor2.setLocation("Dhaka");
                employeeRepository.save(doctor2);

                // 3. Create Default Rescue Team Member
                RescueTeam rescue1 = new RescueTeam();
                rescue1.setFirstName("Rescue");
                rescue1.setLastName("One");
                rescue1.setUsername("rescue1");
                rescue1.setPassword("1234");
                rescue1.setPhone("01811122233");
                rescue1.setLocation("Dhaka");
                rescue1.setStatus("Active");
                rescueTeamRepository.save(rescue1);

                // 4. Create Default Customer
                Customer customer = new Customer();
                customer.setFirstName("Demo");
                customer.setLastName("Customer");
                customer.setUsername("customer1");
                customer.setPassword("pass123");
                customer.setEmail("customer1@example.com");
                customer.setPhone("01811100001");
                customer.setGender("M");
                customerRepository.save(customer);

                // 5. Seed Starter Products
                if (productRepository.count() == 0) {
                    Product p1 = new Product();
                    p1.setName("Premium Dog Food (5kg)");
                    p1.setCategory("Food");
                    p1.setPrice(2500.0);
                    p1.setQuantity(50);
                    p1.setDescription("Nutritious and balanced diet for adult dogs.");
                    productRepository.save(p1);

                    Product p2 = new Product();
                    p2.setName("Herbal Cat Shampoo");
                    p2.setCategory("Accessories");
                    p2.setPrice(650.0);
                    p2.setQuantity(30);
                    p2.setDescription("Gentle flea and tick protection shampoo for cats.");
                    productRepository.save(p2);

                    Product p3 = new Product();
                    p3.setName("Pet Health Multivitamin");
                    p3.setCategory("Medicine");
                    p3.setPrice(1200.0);
                    p3.setQuantity(40);
                    p3.setDescription("Essential multivitamins for pet immunity.");
                    productRepository.save(p3);
                }

                // 6. Seed Starter Adoption Pets
                if (adoptionPetRepository.count() == 0) {
                    AdoptionPet pet1 = new AdoptionPet();
                    pet1.setName("Milo");
                    pet1.setCategory("Dog");
                    pet1.setDescription("Friendly 6-month-old golden retriever puppy. Vaccinated and healthy.");
                    pet1.setStatus("Available");
                    adoptionPetRepository.save(pet1);

                    AdoptionPet pet2 = new AdoptionPet();
                    pet2.setName("Luna");
                    pet2.setCategory("Cat");
                    pet2.setDescription("Calm and loving 1-year-old rescue cat ready for adoption.");
                    pet2.setStatus("Available");
                    adoptionPetRepository.save(pet2);
                }

                log.info("Default cloud data seeded successfully!");
            }
        } catch (Exception e) {
            log.warn("Data initialization skipped or already present: " + e.getMessage());
        }
    }
}
