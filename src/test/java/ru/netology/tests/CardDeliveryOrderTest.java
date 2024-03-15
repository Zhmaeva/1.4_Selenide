package ru.netology.tests;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.conditions.Text;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.*;

public class CardDeliveryOrderTest {

    LocalDate date = LocalDate.now();

    //end to end (состояние загрузки не более 15сек)
    @Test
    void shouldSendCardOrderForm() throws InterruptedException {
        open("http://localhost:9999/");
        $("[data-test-id='city'] input").setValue("Москва");
        $("[data-test-id='date'] input").setValue(String.valueOf(date.plusDays(3)));
        $("[data-test-id='name'] input").setValue("Иван Иванов-Петров");
        $("[data-test-id='phone'] input").setValue("+70000000000");
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Забронировать")).click();
        // $("fieldset").shouldBe(disabled);
        $("[data-test-id='notification']").shouldBe(visible, Duration.ofSeconds(15));



    }
    //отправка пустой формы
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
