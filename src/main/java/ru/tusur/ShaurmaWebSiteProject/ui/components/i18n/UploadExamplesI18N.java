package ru.tusur.ShaurmaWebSiteProject.ui.components.i18n;

import com.vaadin.flow.component.upload.UploadI18N;

import java.util.Arrays;

public class UploadExamplesI18N extends UploadI18N {
    public UploadExamplesI18N() {
        setDropFiles(new DropFiles().setOne("Перетащите файл сюда").setMany("Перетащите сюда файлы"));
        setAddFiles(new AddFiles().setOne("Загрузить файл...")
                .setMany("Загрузить файлы..."));
        setError(new Error().setTooManyFiles("Слишком много файлов.")
                .setFileIsTooBig("Файл слишком большой.")
                .setIncorrectFileType("Неправильный тип файла."));
        setUploading(new Uploading()
                .setStatus(new Uploading.Status().setConnecting("Подключение...")
                        .setStalled("Заглох")
                        .setProcessing("Обработка файла...").setHeld("В очереди"))
                .setRemainingTime(new Uploading.RemainingTime()
                        .setPrefix("оставшееся время: ")
                        .setUnknown("оставшееся время неизвестно"))
                .setError(new Uploading.Error()
                        .setServerUnavailable(
                                "Не удалось загрузить. Повторите попытку позже.")
                        .setUnexpectedServerError(
                                "Загрузка не удалась из-за ошибки сервера.")
                        .setForbidden("Загрузка запрещена")));
        setUnits(new Units().setSize(Arrays.asList("B", "kB", "MB", "GB", "TB",
                "PB", "EB", "ZB", "YB")));
    }
}