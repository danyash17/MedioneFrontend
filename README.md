# Medione - Healthcare CRM based on FHIR standard

Medione is a healthcare CRM designed to work within a single healthcare unit. It is based on the FHIR standard and has been developed from scratch using Java, Spring Boot, Hibernate and Vaadin. This project was developed by a single developer as a university thesis.

## Accessibility and Distribution

Medione is a software system consisting of a client application and a backend application. The source code of the client application is publicly available, but the backend application is closed source and not available for public use. The entire system is restricted to free use, and any attempt to redistribute or modify the project without permission is strictly prohibited. The developer retains all rights to the project and reserves the right to take legal action against any unauthorised use or distribution of the software.

## Key Features

- Authorization and Registration: Users can create an account and proceed to a home screen. The system supports 2-factor authentication via SMS or Google Auth app for added security.



https://github.com/danyash17/MedioneFrontend/assets/71978402/0faf6b1e-51eb-474d-af76-74cc2d11495b


  
- Profile Settings: Users can maintain their profile information, including personal information, qualification & occupation settings for doctors, and profile security settings.


https://github.com/danyash17/MedioneFrontend/assets/71978402/34b51346-ab7e-4db1-a598-d550617d8e5a



- Medical Record: A patient's personal entity containing all information about their illnesses and surgeries. It has a validity period and information about the patient's address.

  

https://github.com/danyash17/MedioneFrontend/assets/71978402/bfad54b8-4432-49c8-8904-986e7d1de422



- Booking System: A sub-module that allows patients to book a visit to a specific doctor. Patients can select a particular doctor's specialty, choose a doctor from a list, book a time and date, and provide a reason. Doctors can manage all upcoming visits, process requests and send bookings to the archive.


https://github.com/danyash17/MedioneFrontend/assets/71978402/ff6823d8-7918-4b48-8ec9-34fc062582f2

  

- Documental Module: The largest and most complex subsystem of the application. It allows users to have a personal repository of all medical documentation in a FHIR resource format. Medione supports full integrity with the resources *Observation*, *Procedure*, *Diagnostic Report* and *Medication Request*. It is recommended to embed links to international medical classifiers in these types of documents. Some of these are LOINC, SNOMED, ICD, Orpanet.
The submodule also includes the ability for doctors to synthesise a prescription document. The generation form submits a registered patient, generates a unique prescription identifier, and adjusts date properties. The drug search component is connected to an official state drug registry of the Republic of Belarus. It contains all necessary details such as trade and international names, manufacturer and even instructions for use for both doctor and patient. After selecting a specific drug, the user can configure the drug's form, quantity, frequency properties or leave an individual comment. The prescription is then available in digital and printed form. The digital document has legal force in the healthcare information systems of the Republic of Belarus and can be used in existing hospital systems.


https://github.com/danyash17/MedioneFrontend/assets/71978402/8a93a2d2-c40b-46f8-98a9-79eeaf647efc


https://github.com/danyash17/MedioneFrontend/assets/71978402/4971c55a-e695-4bb4-9a35-41fb3fb14a93


