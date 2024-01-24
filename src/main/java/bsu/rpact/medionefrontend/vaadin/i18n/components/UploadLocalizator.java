package bsu.rpact.medionefrontend.vaadin.i18n.components;

import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.UploadI18N;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;

public class UploadLocalizator extends UploadI18N implements LocaleChangeObserver {

    private TextField hook = new TextField();

    public UploadLocalizator() {
        init();
    }

    private void init() {
        setDropFiles(new DropFiles()
                .setOne(hook.getTranslation("profile.upload.drop_file_here")));
        setAddFiles(new AddFiles()
                .setOne(hook.getTranslation("profile.upload.choose_file")));
        setCancel(hook.getTranslation("profile.upload.cancel"));
        setError(new Error()
                .setFileIsTooBig(hook.getTranslation("profile.upload.file_too_big"))
                .setIncorrectFileType(hook.getTranslation("profile.upload.incorrect_file_type")));
        setUploading(new Uploading()
                .setStatus(new Uploading.Status()
                        .setConnecting(hook.getTranslation("profile.upload.connecting"))
                        .setStalled(hook.getTranslation("profile.upload.stalled"))
                        .setProcessing(hook.getTranslation("profile.upload.processing"))
                        .setHeld(hook.getTranslation("profile.upload.held")))
                .setRemainingTime(new Uploading.RemainingTime()
                        .setPrefix(hook.getTranslation("profile.upload.time_remaining"))
                        .setUnknown(hook.getTranslation("profile.upload.time_unknown")))
                .setError(new Uploading.Error()
                        .setServerUnavailable(hook.getTranslation("profile.upload.server_unavailable"))
                        .setUnexpectedServerError(hook.getTranslation("profile.upload.server_error"))
                        .setForbidden(hook.getTranslation("profile.upload.forbidden"))));
    }

    @Override
    public void localeChange(LocaleChangeEvent localeChangeEvent) {
        init();
    }
}
