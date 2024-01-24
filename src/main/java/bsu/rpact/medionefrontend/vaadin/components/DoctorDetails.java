package bsu.rpact.medionefrontend.vaadin.components;

import bsu.rpact.medionefrontend.entity.Doctor;
import bsu.rpact.medionefrontend.pojo.RepresentativeDoctorSpecialityPojo;
import bsu.rpact.medionefrontend.pojo.other.DoctorPhotoUrlContainer;
import bsu.rpact.medionefrontend.service.DoctorSpecialityService;
import bsu.rpact.medionefrontend.utils.ImageUtils;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import org.vaadin.addons.badge.Badge;

public class DoctorDetails extends VerticalLayout implements LocaleChangeObserver {

    private String currentHospital = getTranslation("profile.current_hospital");
    private String instituteOfAccreditation = getTranslation("profile.institute_of_accreditation");
    private String workExperience = getTranslation("profile.work_experience");
    private String speciality = getTranslation("profile.speciality");
    private final DoctorSpecialityService doctorSpecialityService;
    private final ImageUtils imageUtils;

    public DoctorDetails(DoctorSpecialityService doctorSpecialityService, ImageUtils imageUtils, Doctor doctor) {
        this.doctorSpecialityService = doctorSpecialityService;
        this.imageUtils = imageUtils;
        setupDoctorSection(doctor);
    }

    private void setupDoctorSection(Doctor doctor) {
        DoctorPhotoUrlContainer container = initContainer(doctor);
        Grid<RepresentativeDoctorSpecialityPojo> doctorSpecialityGrid =
                new Grid<>(RepresentativeDoctorSpecialityPojo.class, false);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        Image image = new Image(container.getPhotoUrl(), "");
        image.setMaxWidth("530px");
        image.setMaxHeight("630px");
        setMinWidth("700px");
        add(image);
        add(new H2(container.getDoctor().getCredentials().getFirstName() + " " +
                container.getDoctor().getCredentials().getPatronymic() + " " +
                container.getDoctor().getCredentials().getLastName()));
        Badge badgeHospital = new Badge();
        badgeHospital.setPrimary(true);
        badgeHospital.setPill(true);
        badgeHospital.setText(container.getDoctor().getHospital());
        badgeHospital.setVariant(Badge.BadgeVariant.NORMAL);
        badgeHospital.setIcon(VaadinIcon.HOSPITAL.create());
        add(new HorizontalLayout(new Label(currentHospital), badgeHospital));
        add(new H4(container.getDoctor().getCommonInfo()));
        doctorSpecialityGrid.addColumn(doctorSpeciality -> doctorSpeciality.getSpeciality()).setHeader(speciality);
        doctorSpecialityGrid.addColumn(RepresentativeDoctorSpecialityPojo::getInstitute).setHeader(instituteOfAccreditation);
        doctorSpecialityGrid.addColumn(RepresentativeDoctorSpecialityPojo::getExperience).setHeader(workExperience);
        doctorSpecialityGrid.setItems(doctorSpecialityService.getDoctorSpecialities(container.getDoctor().getId()));
        doctorSpecialityGrid.setAllRowsVisible(true);
        add(doctorSpecialityGrid);
    }

    private DoctorPhotoUrlContainer initContainer(Doctor doctor) {
        DoctorPhotoUrlContainer container = new DoctorPhotoUrlContainer();
        container.setDoctor(doctor);
        String fullName = doctor.getCredentials().getFirstName() +
                doctor.getCredentials().getPatronymic() +
                doctor.getCredentials().getLastName();
        container.setPhotoUrl(imageUtils.chacheByteArrToImageDoctor(doctor.getDoctorPhoto(), fullName));
        return container;
    }

    @Override
    public void localeChange(LocaleChangeEvent localeChangeEvent) {
        currentHospital = getTranslation("profile.current_hospital");
        instituteOfAccreditation = getTranslation("profile.institute_of_accreditation");
        workExperience = getTranslation("profile.work_experience");
        speciality = getTranslation("profile.speciality");
    }
}
