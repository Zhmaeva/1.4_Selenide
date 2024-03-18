package ru.netology.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.support.Color;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static com.codeborne.selenide.Condition.*;
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
    String emptyDateText = "Неверно введена дата";
    String invalidCityText = "Доставка в выбранный город недоступна";
    String invalidDateText = "Заказ на выбранную дату невозможен";
    String invalidNameText = "Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы.";
    String invalidPhoneText = "Телефон указан неверно. Должно быть 11 цифр, например, +79012345678.";

    String calculateDate(int days) {
        return LocalDate.now().plusDays(days).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    void clearField(String selector) {
        $(selector).sendKeys(Keys.CONTROL + "A");
        $(selector).sendKeys(Keys.BACK_SPACE);
    }

    @BeforeEach
    void prepareForTest() {
        open("http://localhost:9999/");
        clearField(validDateSelector);
    }

    //end to end (состояние загрузки не более 15сек)
    @Test
    void shouldSendCardOrderForm() {
        String date = calculateDate(3);
        $(validCitySelector).setValue("Москва");
        clearField(validDateSelector);
        $(validDateSelector).setValue(date);
        $(validNameSelector).setValue("Иван Иванов-Петров");
        $(validPhoneSelector).setValue("+70000000000");
        $(validAgreementSelector).click();
        $$("button").find(exactText("Забронировать")).click();
        $(notificationTitleSelector).shouldBe(visible, Duration.ofSeconds(15)).shouldHave(text(notificationTitleText));
        $(notificationContentSelector).shouldBe(visible, Duration.ofSeconds(15)).shouldHave(text(notificationContentText + date));
    }

    //отправка пустой формы
    @Test
    void shouldNotSendEmptyForm() {
        clearField(validCitySelector);
        clearField(validDateSelector);
        clearField(validNameSelector);
        clearField(validPhoneSelector);
        $$("button").find(exactText("Забронировать")).click();
        $(invalidCitySelector).shouldBe(visible).shouldHave(text(emptyFieldText));
    }

    //поле ГОРОД не заполнено
    @Test
    void shouldNotSendFormWithEmptyCityField() {
        String date = calculateDate(3);
        clearField(validCitySelector);
        $(validDateSelector).setValue(date);
        $(validNameSelector).setValue("Петрова-Сидорова Елизавета");
        $(validPhoneSelector).setValue("70000000000");
        $(validAgreementSelector).click();
        $$("button").find(exactText("Забронировать")).click();
        $(validCitySelector + ".input__sub").shouldBe(hidden);
        $(invalidCitySelector).shouldBe(visible).shouldHave(text(emptyFieldText));
    }

    //поле ДАТА ВСТРЕЧИ не заполнено
    @Test
    void shouldNotSendFormWithEmptyDateField() {
        $(validCitySelector).setValue("Воронеж");
        clearField(validDateSelector);
        $(validNameSelector).setValue("Петрова-Сидорова Елизавета");
        $(validPhoneSelector).setValue("+70000000000");
        $(validAgreementSelector).click();
        $$("button").find(exactText("Забронировать")).click();
        $(validDateSelector + ".input__sub").shouldBe(hidden);
        $(invalidDateSelector).shouldBe(visible).shouldHave(text(emptyDateText));
    }

    //поле ФИО не заполнено
    @Test
    void shouldNotSendFormWithEmptyNameField() {
        String date = calculateDate(10);
        $(validCitySelector).setValue("Петропавловск-Камчатский");
        $(validDateSelector).setValue(date);
        clearField(validNameSelector);
        $(validPhoneSelector).setValue("+70000000000");
        $(validAgreementSelector).click();
        $$("button").find(exactText("Забронировать")).click();
        $(validNameSelector + ".input__sub").shouldBe(hidden);
        $(invalidNameSelector).shouldBe(visible).shouldHave(text(emptyFieldText));
    }

    //поле ТЕЛЕФОН не заполнено
    @Test
    void shouldNotSendFormWithEmptyPhoneField() {
        String date = calculateDate(14);
        $(validCitySelector).setValue("Южно-Сахалинск");
        $(validDateSelector).setValue(date);
        $(validNameSelector).setValue("Мария-Виктория Волхонская");
        clearField(validPhoneSelector);
        $(validAgreementSelector).click();
        $$("button").find(exactText("Забронировать")).click();
        $(validPhoneSelector + ".input__sub").shouldBe(hidden);
        $(invalidPhoneSelector).shouldBe(visible).shouldHave(text(emptyFieldText));
    }

    //чекбокс не активен
    @Test
    void shouldNotSendFormWhenCheckboxNotActive() {
        String date = calculateDate(7);
        $(validCitySelector).setValue("Ханты-Мансийск");
        $(validDateSelector).setValue(date);
        $(validNameSelector).setValue("Мария-Виктория Волхонская");
        $(validPhoneSelector).setValue("+70000000000");
        $$("button").find(exactText("Забронировать")).click();
        $(notificationTitleSelector).shouldBe(hidden);
        $(invalidAgreementSelector).shouldBe(visible);
        String color = Color.fromString($(invalidAgreementSelector).getCssValue("color")).asHex();
        Assertions.assertEquals("#ff5c5c", color);
    }

    //поле ГОРОД заполнено не субъектом РФ
    @Test
    void shouldNotSendFormWhenCityNotRF() {
        String date = calculateDate(3);
        $(validCitySelector).setValue("Лондон");
        $(validDateSelector).setValue(date);
        $(validNameSelector).setValue("Мария-Виктория Волхонская");
        $(validPhoneSelector).setValue("+70000000000");
        $(validAgreementSelector).click();
        $$("button").find(exactText("Забронировать")).click();
        $(validCitySelector + ".input__sub").shouldBe(hidden);
        $(invalidCitySelector).shouldBe(visible).shouldHave(text(invalidCityText));
    }

    //поле ДАТА заполнено ранее трёх дней с текущей даты
    @Test
    void shouldNotSendFormWhenDateLess3Days() {
        String date = calculateDate(2);
        $(validCitySelector).setValue("Челябинск");
        $(validDateSelector).setValue(date);
        $(validNameSelector).setValue("Мария-Виктория Волхонская");
        $(validPhoneSelector).setValue("+70000000000");
        $(validAgreementSelector).click();
        $$("button").find(exactText("Забронировать")).click();
        $(validDateSelector + ".input__sub").shouldBe(hidden);
        $(invalidDateSelector).shouldBe(visible, Duration.ofSeconds(4)).shouldHave(text(invalidDateText));
    }

    //поле ФИО заполнено на иностранном языке
    @Test
    void shouldNotSendFormWhenNameIsNotRus() {
        String date = calculateDate(3);
        $(validCitySelector).setValue("Москва");
        $(validDateSelector).setValue(date);
        $(validNameSelector).setValue("María-Jose Carreño Quiñones");
        $(validPhoneSelector).setValue("+70000000000");
        $(validAgreementSelector).click();
        $$("button").find(exactText("Забронировать")).click();
        $(validNameSelector + ".input__sub").shouldBe(hidden);
        $(invalidNameSelector).shouldBe(visible).shouldHave(text(invalidNameText));
    }

    //поле ФИО заполнено символами
    @Test
    void shouldNotSubmitFormIfNameIsFilledWithCharacters() {
        String date = calculateDate(3);
        $(validCitySelector).setValue("Москва");
        $(validDateSelector).setValue(date);
        $(validNameSelector).setValue(".");
        $(validPhoneSelector).setValue("+70000000000");
        $(validAgreementSelector).click();
        $$("button").find(exactText("Забронировать")).click();
        $(validNameSelector + ".input__sub").shouldBe(hidden);
        $(invalidNameSelector).shouldBe(visible).shouldHave(text(invalidNameText));
    }

    //поле ФИО заполнено цифрами
    @Test
    void shouldNotSubmitFormIfNameIsFilledWithNumbers() {
        String date = calculateDate(3);
        $(validCitySelector).setValue("Москва");
        $(validDateSelector).setValue(date);
        $(validNameSelector).setValue("123456789");
        $(validPhoneSelector).setValue("+70000000000");
        $(validAgreementSelector).click();
        $$("button").find(exactText("Забронировать")).click();
        $(validNameSelector + ".input__sub").shouldBe(hidden);
        $(invalidNameSelector).shouldBe(visible).shouldHave(text(invalidNameText));
    }

    //поле ФИО заполнено со специфичными буквами (например ё)
    @Test
    void shouldNotSubmitFormIfNameIsFilledWithSpecialSymbols() {
        String date = calculateDate(3);
        $(validCitySelector).setValue("Москва");
        $(validDateSelector).setValue(date);
        $(validNameSelector).setValue("ё");
        $(validPhoneSelector).setValue("+70000000000");
        $(validAgreementSelector).click();
        $$("button").find(exactText("Забронировать")).click();
        $(validNameSelector + ".input__sub").shouldBe(hidden);
        $(invalidNameSelector).shouldBe(visible).shouldHave(text(invalidNameText));
    }

    //поле ФИО заполнено пробелами
    @Test
    void shouldNotSubmitFormIfNameIsFilledWithSpace() {
        String date = calculateDate(3);
        $(validCitySelector).setValue("Москва");
        $(validDateSelector).setValue(date);
        $(validNameSelector).setValue("         ");
        $(validPhoneSelector).setValue("+70000000000");
        $(validAgreementSelector).click();
        $$("button").find(exactText("Забронировать")).click();
        $(validNameSelector + ".input__sub").shouldBe(hidden);
        $(invalidNameSelector).shouldBe(visible).shouldHave(text(emptyFieldText));
    }

    //в поле ТЕЛЕФОН меньше 11 цифр
    @Test
    void shouldNotSubmitFormIfPhoneLess11Numbers() {
        String date = calculateDate(3);
        $(validCitySelector).setValue("Москва");
        $(validDateSelector).setValue(date);
        $(validNameSelector).setValue("Петров Петр");
        $(validPhoneSelector).setValue("+7000000000");
        $(validAgreementSelector).click();
        $$("button").find(exactText("Забронировать")).click();
        $(validPhoneSelector + ".input__sub").shouldBe(hidden);
        $(invalidPhoneSelector).shouldBe(visible).shouldHave(text(invalidPhoneText));
    }

    //в поле ТЕЛЕФОН больше 11 цифр
    @Test
    void shouldNotSubmitFormIfPhoneMore11Numbers() {
        String date = calculateDate(3);
        $(validCitySelector).setValue("Москва");
        $(validDateSelector).setValue(date);
        $(validNameSelector).setValue("Петров Петр");
        $(validPhoneSelector).setValue("+7000000000000");
        $(validAgreementSelector).click();
        $$("button").find(exactText("Забронировать")).click();
        $(validPhoneSelector + ".input__sub").shouldBe(hidden);
        $(invalidPhoneSelector).shouldBe(visible).shouldHave(text(invalidPhoneText));
    }

    //в поле ТЕЛЕФОН нет символа +
    @Test
    void shouldNotSubmitFormIfPhoneWithoutPlus() {
        String date = calculateDate(3);
        $(validCitySelector).setValue("Москва");
        $(validDateSelector).setValue(date);
        $(validNameSelector).setValue("Петров Петр");
        $(validPhoneSelector).setValue("70000000000");
        $(validAgreementSelector).click();
        $$("button").find(exactText("Забронировать")).click();
        $(validPhoneSelector + ".input__sub").shouldBe(hidden);
        $(invalidPhoneSelector).shouldBe(visible).shouldHave(text(invalidPhoneText));
    }

    //в поле ТЕЛЕФОН символ + в конце
    @Test
    void shouldNotSubmitFormIfPhoneHasPlusAtTheEnd() {
        String date = calculateDate(3);
        $(validCitySelector).setValue("Москва");
        $(validDateSelector).setValue(date);
        $(validNameSelector).setValue("Петров Петр");
        $(validPhoneSelector).setValue("70000000000+");
        $(validAgreementSelector).click();
        $$("button").find(exactText("Забронировать")).click();
        $(validPhoneSelector + ".input__sub").shouldBe(hidden);
        $(invalidPhoneSelector).shouldBe(visible).shouldHave(text(invalidPhoneText));
    }

    //Задача №2: взаимодействие с комплексными элементами
    @Test
    void shouldSelectCityFromTheDropdownList() {
        String date = calculateDate(4);
        String city = "Екатеринбург";
        $(validCitySelector).setValue("ка");
        $(".input__popup .menu").should(visible);
        $$(".input__popup .menu .menu-item").find(text(city)).click();
        $(validDateSelector).setValue(date);
        $(validNameSelector).setValue("Петров Петр");
        $(validPhoneSelector).setValue("+70000000000");
        $(validAgreementSelector).click();
        $$("button").find(exactText("Забронировать")).click();
        $(notificationTitleSelector).shouldBe(visible, Duration.ofSeconds(15)).shouldHave(text(notificationTitleText));
        $(notificationContentSelector).shouldBe(visible, Duration.ofSeconds(15)).shouldHave(text(notificationContentText + date));
        Assertions.assertEquals(city, $(validCitySelector).getValue());
    }

    @Test
    void shouldSelectDateForTheWeekAheadThroughTheCalendarTool() {
        int daysAhead = 7;
        LocalDate date = LocalDate.now().plusDays(daysAhead);
        String dateDelivery = calculateDate(daysAhead);
        String day = date.format(DateTimeFormatter.ofPattern("dd"));
        String month = date.format(DateTimeFormatter.ofPattern("LLLL", Locale.forLanguageTag("ru")));
        String year = date.format(DateTimeFormatter.ofPattern("yyyy"));
        $(validCitySelector).setValue("Екатеринбург");
        $(".input__box .input__icon .icon-button").click();
        $(".popup .calendar").should(visible);
        String name = $(".calendar__name").should(visible).text().toLowerCase();

        if (!name.equals(month + " " + year)) {
            $("[data-step='1']").click();
        }

        $$(".calendar__layout .calendar__day").find(text(day)).click();
        $(validNameSelector).setValue("Петров Петр");
        $(validPhoneSelector).setValue("+70000000000");
        $(validAgreementSelector).click();
        $$("button").find(exactText("Забронировать")).click();
        $(notificationTitleSelector).shouldBe(visible, Duration.ofSeconds(15)).shouldHave(text(notificationTitleText));
        $(notificationContentSelector).shouldBe(visible, Duration.ofSeconds(15)).shouldHave(text(notificationContentText + dateDelivery));
        Assertions.assertEquals(dateDelivery, $(validDateSelector).getValue());

    }
}
