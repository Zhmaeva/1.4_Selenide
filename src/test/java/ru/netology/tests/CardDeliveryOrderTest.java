package ru.netology.tests;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.conditions.Text;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.*;

public class CardDeliveryOrderTest {

    //selectors
    String validDateSelector = "[data-test-id='date'] input";
    String validCitySelector = "[data-test-id='city'] input";
    String validNameSelector = "[data-test-id='name'] input";
    String validPhoneSelector = "[data-test-id='phone'] input";
    String validAgreementSelector = "[data-test-id='agreement'] span.checkbox__box";
    String notificationTitleSelector = "[data-test-id=notification] .notification__title";
    String notificationContentSelector = "[data-test-id=notification] .notification__content";
    String invalidCitySelector = "[data-test-id=city].input_invalid .input__sub";
    String invalidDateSelector = "[data-test-id=date] .input_invalid .input__sub";
    String invalidNameSelector = "[data-test-id=name].input_invalid .input__sub";
    String invalidPhoneSelector = "[data-test-id=phone].input_invalid .input__sub";
    String invalidAgreementSelector = "[data-test-id=agreement].input_invalid";

    //messages
    String notificationTitleText = "Успешно!";
    String notificationContentText = "Встреча успешно забронирована на ";
    String emptyFieldText = "Поле обязательно для заполнения";

    String calculateDate(int days) {
        return LocalDate.now().plusDays(days).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }
    @BeforeEach
    void setUpAll() {
        open("http://localhost:9999/");
    }

    //end to end (состояние загрузки не более 15сек)
    @Test
    void shouldSendCardOrderForm() {
        String date = calculateDate(3);
        $(validCitySelector).setValue("Москва");
        $(validDateSelector).setValue(date);
        $(validNameSelector).setValue("Иван Иванов-Петров");
        $(validPhoneSelector).setValue("+70000000000");
        $(validAgreementSelector).click();
        $$("button").find(exactText("Забронировать")).click();
        // $("fieldset").shouldBe(disabled); ?
        $(notificationTitleSelector).shouldBe(visible, Duration.ofSeconds(15)).shouldHave(text(notificationTitleText));
        $(notificationContentSelector).shouldBe(visible, Duration.ofSeconds(15)).shouldHave(text(notificationContentText + date));
    }

    //отправка пустой формы
    @Test
    void shouldNotSendEmptyForm() {
        $(validCitySelector).clear();
        $(validDateSelector).clear();
        $(validNameSelector).clear();
        $(validPhoneSelector).clear();
        $(validAgreementSelector).isEnabled();
        $$("button").find(exactText("Забронировать")).click();
        $(invalidCitySelector).shouldBe(visible).shouldHave(text(emptyFieldText));
    }

    //поле ГОРОД не заполнено
    //поле ДАТА ВСТРЕЧИ не заполнено
    //поле ФИО не заполнено
    //поле ТЕЛЕФОН не заполнено
    //чекбокс не активен
    //поле ГОРОД заполнено не субъектом РФ
    //поле ДАТА заполнено ранее трёх дней с текущей даты
    //поле ФИО заполнено на иностранном языке
    //поле ФИО заполнено символами
    //поле ФИО заполнено цифрами
    //поле ФИО заполнено со специфичными буквами (например ё)
    //поле ФИО заполнено пробелами
    //в поле ТЕЛЕФОН меньше 11 цифр
    //в поле ТЕЛЕФОН больше 11 цифр
    //в поле ТЕЛЕФОН нет символа +
    //в поле ТЕЛЕФОН символ + в конце

}
