# Medione - Healthcare CRM based on FHIR standard

Medione is a healthcare CRM designed to work within a single healthcare unit. It is based on the FHIR standard and developed from scratch using Java, Spring Boot, Hibernate, and Vaadin. This project was developed by a single developer as a university thesis.

## Accessibility and Distribution

Medione is a software system that consists of both a client application and a backend application. The source code of the client application is publicly accessible, but the backend application is closed source and not available for public use. The entire system is restricted to free use, and any attempt to distribute or modify the project without permission is strictly prohibited. The developer retains all rights to the project and reserves the right to take legal action against any unauthorized use or distribution of the software.

## Key Features

- Authorization and Registration: Users can create an account and proceed to a home screen. The system supports 2-factor authentication via SMS or Google Auth app for added security.



https://github.com/danyash17/MedioneFrontend/assets/71978402/0faf6b1e-51eb-474d-af76-74cc2d11495b


  
- Profile Settings: Users can maintain their profile information, including personal information, qualification & occupation settings for doctors, and profile security settings.


https://github.com/danyash17/MedioneFrontend/assets/71978402/34b51346-ab7e-4db1-a598-d550617d8e5a



- Medical Record: Personal patient entity containing all information about their illnesses and operations. It has a validity period and information about the patient's address.

  

https://github.com/danyash17/MedioneFrontend/assets/71978402/bfad54b8-4432-49c8-8904-986e7d1de422



- Booking System: A submodule that allows patients to book a visit to a specific doctor. Patients can choose a particular doctor's specialty, pick a doctor from a list, book a time and date, and provide a reason. Doctors can maintain all upcoming visits, process requests, and send bookings to the archive.


https://github.com/danyash17/MedioneFrontend/assets/71978402/ff6823d8-7918-4b48-8ec9-34fc062582f2

  

- Documental Module: The biggest and most complex subsystem of the application. It allows users to have a personal storage of all medical documental data in a FHIR resource format. Medione supports full integrity with resources *Observation*, *Procedure*, *Diagnostic report* and *Medication request*. It is recommended to embed international medical classificator links to these kinds of documents. Some of those are LOINC, SNOMED, ICD, Orpanet.
The submodule also includes the ability for doctors to synthesize a medical prescription document. The generation form submits a registered patient, generates unique prescription identifier, adjusts date properties. The medication search component is connceted to an official state drug registry of the Republic of Belarus. It contains all necessary details such as trade and international names, manufacturer, and even instructions for use for doctor and patient. After selecting specific medicine the user can configure medication form, amount, frequency properties, or leave custom comment. After that the medical prescription becomes accessible in digital and printed forms. The digital document has legal force in the information systems of health care of the Republic of Belarus and can be used in existing hospital systems.


https://github.com/danyash17/MedioneFrontend/assets/71978402/8a93a2d2-c40b-46f8-98a9-79eeaf647efc


https://github.com/danyash17/MedioneFrontend/assets/71978402/4971c55a-e695-4bb4-9a35-41fb3fb14a93


