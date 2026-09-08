// Script to generate realistic dummy data for Pets Care Management System
const fs = require('fs');

const maleNames = ['Arif', 'Sakib', 'Tanzim', 'Rafi', 'Hasan', 'Emon', 'Sabbir', 'Sohel', 'Fahim', 'Ratul', 'Nabil', 'Parvez', 'Rayhan', 'Rony', 'Sujon', 'Towhid', 'Siam', 'Mehedi', 'Ashik', 'Shuvo', 'Sajid', 'Shahadat'];
const femaleNames = ['Anika', 'Sadia', 'Tasnim', 'Nusrat', 'Farzana', 'Maria', 'Sumaiya', 'Tania', 'Puja', 'Ritu', 'Brishti', 'Mim', 'Zerin', 'Fatema', 'Jannat', 'Sharmin', 'Afroza', 'Riya', 'Sabrina', 'Mehzabien', 'Farhana', 'Nadia'];
const lastNames = ['Islam', 'Rahman', 'Hossain', 'Ahmed', 'Chowdhury', 'Khan', 'Das', 'Roy', 'Saha', 'Hasan', 'Miah', 'Uddin'];
const locations = ['Dhaka', 'Khulna', 'Rajshahi', 'Sylhet', 'Chittagong', 'Barisal', 'Rangpur', 'Mymensingh'];
const animalCategories = ['Dog', 'Cat', 'Bird', 'Rabbit', 'Hamster'];

function randomChoice(arr) {
    return arr[Math.floor(Math.random() * arr.length)];
}

function randInt(min, max) {
    return Math.floor(Math.random() * (max - min + 1)) + min;
}

function getRandomName() {
    const isMale = Math.random() > 0.5;
    const gender = isMale ? 'M' : 'F';
    const first = randomChoice(isMale ? maleNames : femaleNames);
    const last = randomChoice(lastNames);
    return { first, last, gender, fullName: `${first} ${last}` };
}

let sql = `-- ==========================================================
-- Pets Care Management System - Realistic Data Population
-- Contains >= 22 records per table to fulfill academic requirements
-- ==========================================================\n\nUSE petscare;\nSET FOREIGN_KEY_CHECKS=0;\n\n`;

const numRecords = 22;

// 1. Admin (1 default record will be added manually, so we don't generate 22 admins)
sql += `-- Admin Data\n`;
sql += `INSERT INTO Admins (FirstName, LastName, Username, Password, Phone, Gender, Age) VALUES ('Super', 'Admin', 'admin', 'admin123', '01711122233', 'M', 35);\n`;

// 2. Customer (22 records)
sql += `\n-- Customer Data\n`;
for (let i = 1; i <= numRecords; i++) {
    const p = getRandomName();
    sql += `INSERT INTO Customer (FirstName, LastName, Username, Password, Email, Phone, Gender) VALUES ('${p.first}', '${p.last}', 'customer${i}', 'pass123', 'cust${i}@example.com', '01811${100000+i}', '${p.gender}');\n`;
}

// 3. Employee (Doctor/Staff) (22 records)
sql += `\n-- Employee Data\n`;
sql += `INSERT INTO Employee (AdminID, FirstName, LastName, Username, Password, Role, Specialty, Phone, Gender, Age, Salary, WorkingHours, Location) VALUES (1, 'Rupa', 'Doctor', 'rupa', '1234', 'Doctor', 'Surgeon', '01911122233', 'F', 28, 50000, 8, 'Dhaka');\n`;
for (let i = 1; i <= numRecords; i++) {
    const p = getRandomName();
    const role = i <= 15 ? 'Doctor' : 'Staff';
    const spec = role === 'Doctor' ? randomChoice(['Surgeon', 'General Vet', 'Dermatologist', 'Cardiologist']) : 'Management';
    sql += `INSERT INTO Employee (AdminID, FirstName, LastName, Username, Password, Role, Specialty, Phone, Gender, Age, Salary, WorkingHours, Location) VALUES (1, '${p.first}', '${p.last}', 'emp${i}', 'pass123', '${role}', '${spec}', '01911${100000+i}', '${p.gender}', ${randInt(25, 60)}, ${randInt(30000, 80000)}, ${randInt(6, 12)}, '${randomChoice(locations)}');\n`;
}

// 4. Rescue Team (22 records)
sql += `\n-- Rescue Team Data\n`;
sql += `INSERT INTO RescueTeam (FirstName, LastName, Username, Password, Phone, Location, Status) VALUES ('Rescue', 'One', 'rescue1', '1234', '01611122233', 'Dhaka', 'Active');\n`;
for (let i = 1; i <= numRecords; i++) {
    const p = getRandomName();
    sql += `INSERT INTO RescueTeam (FirstName, LastName, Username, Password, Phone, Location, Status) VALUES ('${p.first}', '${p.last}', 'rescue${i}', 'pass123', '01611${100000+i}', '${randomChoice(locations)}', '${randomChoice(['Active', 'On Mission', 'Off Duty'])}');\n`;
}

// 5. Pet (22 records)
sql += `\n-- Pet Data\n`;
for (let i = 1; i <= numRecords; i++) {
    const cat = randomChoice(animalCategories);
    sql += `INSERT INTO Pet (AdminID, Pet_Name, Species, Pet_Type, Age, Price, ImagePath) VALUES (1, 'Pet${i}', '${cat}', '${cat}', '${randInt(1, 5)} months', ${randInt(1000, 5000)}, 'default.jpg');\n`;
}

// 6. Product (22 records)
sql += `\n-- Product Data\n`;
for (let i = 1; i <= numRecords; i++) {
    sql += `INSERT INTO Products (Name, Category, Price, Quantity, Description) VALUES ('Premium ${randomChoice(animalCategories)} Food ${i}', 'Food', ${randInt(500, 3000)}, ${randInt(10, 100)}, 'High quality nutritional food');\n`;
}

// 7. Orders (22 records)
sql += `\n-- Orders Data\n`;
for (let i = 1; i <= numRecords; i++) {
    const p = getRandomName();
    sql += `INSERT INTO Orders (CustomerName, Phone, Location, Items, TotalAmount, Status, PaymentMethod, PaymentStatus) VALUES ('${p.fullName}', '01700${100000+i}', '${randomChoice(locations)}', '[{"name":"Food","price":500,"quantity":2}]', ${randInt(500, 5000)}, '${randomChoice(['Pending', 'Delivered', 'Cancelled'])}', '${randomChoice(['COD', 'SSLCommerz'])}', '${randomChoice(['Pending', 'Paid'])}');\n`;
}

// 8. Emergency Application (22 records)
sql += `\n-- Emergency Application Data\n`;
for (let i = 1; i <= numRecords; i++) {
    const p = getRandomName();
    sql += `INSERT INTO EmergencyApplications (AssignedRescueID, ApplicantName, Phone, AnimalCategory, Description, Location, Area) VALUES (${randInt(1, 20)}, '${p.fullName}', '01511${100000+i}', '${randomChoice(animalCategories)}', 'Injured animal found on the street', '${randomChoice(locations)}', 'Downtown');\n`;
}

// 9. Treatment (22 records)
sql += `\n-- Treatment Data\n`;
for (let i = 1; i <= numRecords; i++) {
    sql += `INSERT INTO Treatments (EmergencyAppID, RescueID, DoctorID, Status, MedicineAssigned, Notes) VALUES (${i}, ${randInt(1, 20)}, ${randInt(1, 15)}, '${randomChoice(['In Progress', 'Completed', 'Critical'])}', 'Painkillers, Antibiotics', 'Patient is recovering well');\n`;
}

// 10. Adoption Pet (22 records)
sql += `\n-- Adoption Pet Data\n`;
for (let i = 1; i <= numRecords; i++) {
    sql += `INSERT INTO AdoptionPets (OriginalTreatmentID, Name, Category, Description, Status) VALUES (${i}, 'Rescued ${randomChoice(animalCategories)} ${i}', '${randomChoice(animalCategories)}', 'Very friendly and looking for a home', '${randomChoice(['Available', 'Adopted', 'Pending'])}');\n`;
}

// 11. Adoption Request (22 records)
sql += `\n-- Adoption Request Data\n`;
for (let i = 1; i <= numRecords; i++) {
    const p = getRandomName();
    sql += `INSERT INTO AdoptionRequests (PetID, ApplicantName, Phone, Email, Reason, Status) VALUES (${i}, '${p.fullName}', '01922${100000+i}', 'adopt${i}@example.com', 'I love animals and have a big backyard', '${randomChoice(['Pending', 'Approved', 'Rejected'])}');\n`;
}

// 12. Donation (22 records)
sql += `\n-- Donation Data\n`;
for (let i = 1; i <= numRecords; i++) {
    const p = getRandomName();
    sql += `INSERT INTO Donation (DonorName, Phone, Amount, Purpose, TransactionID, Status) VALUES ('${p.fullName}', '01733${100000+i}', ${randInt(500, 10000)}, 'Rescue Fund', 'TXN${100000+i}', 'Successful');\n`;
}

// 13. Medicine (22 records)
sql += `\n-- Medicine Data\n`;
for (let i = 1; i <= numRecords; i++) {
    sql += `INSERT INTO Medicines (Name, Type, DosageInfo) VALUES ('VetMeds ${i}', '${randomChoice(['Tablet', 'Syrup', 'Injection'])}', 'Twice a day after meals');\n`;
}

// 14. Payment Config (22 records)
sql += `\n-- Payment Config Data\n`;
for (let i = 1; i <= numRecords; i++) {
    sql += `INSERT INTO PaymentConfig (StoreID, StorePassword, IsLive) VALUES ('store${i}', 'pass${i}', ${randomChoice([true, false])});\n`;
}

sql += `\nSET FOREIGN_KEY_CHECKS=1;\n`;
fs.writeFileSync('realistic_data.sql', sql);
console.log('Successfully generated realistic_data.sql');
