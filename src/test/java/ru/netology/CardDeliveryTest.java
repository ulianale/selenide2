package ru.netology;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.Keys;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Selenide.*;

public class CardDeliveryTest {

    public void deleteMethod() {   // метод удаляет строку c датой
        String dataField = $("[placeholder='Дата встречи']").getValue();
        int r = dataField.length(); // кол-во символов в поле
        $("[placeholder='Дата встречи']").hover().click();
        for (int i = 0; i < r; i++) {
            $("[placeholder='Дата встречи']").sendKeys(Keys.BACK_SPACE);
        }
    }

    public String data(int days) {  // возвращает дату , которая заполнит строку ввода для даты
        LocalDate date = LocalDate.now().plusDays(days);
        return date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    public void twoLetters(String f) { // выбор города из выпадающего списка по двум буквам
        $("[placeholder='Город']").setValue(f);
        ElementsCollection forms = $$(".menu-item__control");
        int m = 0; // счетчик
        for (SelenideElement form : forms) {
            if (f.equalsIgnoreCase(form.getText().substring(0,2))) {
                m++;
                form.click();
                break;
            }
        }
        if (m<1){
            throw new ElementClickInterceptedException("Упс");
        }
    }

    @BeforeEach
    void setUp() {
        //Configuration.holdBrowserOpen = true;
        open("http://localhost:9999");
        deleteMethod();
    }

    @Test
    void shouldFindCity() {
        twoLetters("ка");
    }

    @Test
    void shouldExceptionIfNotFindCity() {
        Assertions.assertThrows(ElementClickInterceptedException.class, () -> {
            twoLetters("аа");
        });
    }

    @Test
    void shouldValidForm() {
        twoLetters("ор");
        //$("[placeholder='Город']").setValue("Мурманск");
        $("[placeholder='Дата встречи']").setValue(data(3));
        $("[name='name']").setValue("Анна-Мария Антонова");
        $("[name='phone']").setValue("+79099001122");
        $(".checkbox").click();
        $(".button__text").click();
        $("[data-test-id='notification']").shouldBe(exactText("Успешно! Встреча успешно забронирована на " + data(3)), Duration.ofSeconds(15));
    }

    @Test
    void shouldEmptyCity() {
        $("[placeholder='Дата встречи']").setValue(data(5));
        $("[name='name']").setValue("Анна-Мария Антонова");
        $("[name='phone']").setValue("+79099001122");
        $(".checkbox").click();
        $(".button__text").click();
        $("[data-test-id='city'].input_invalid .input__sub").shouldHave(exactText("Поле обязательно для заполнения"));
    }

    @Test
    void shouldNotValidCity() {
        $("[placeholder='Город']").setValue("Пекин");
        $("[placeholder='Дата встречи']").setValue(data(20));
        $("[name='name']").setValue("Анна-Мария Антонова");
        $("[name='phone']").setValue("+79099001122");
        $(".checkbox").click();
        $(".button__text").click();
        $("[data-test-id='city'].input_invalid .input__sub").shouldHave(exactText("Доставка в выбранный город недоступна"));
    }

    @Test
    void shouldEmptyDate() {
        $("[placeholder='Город']").setValue("Мурманск");
        $("[name='name']").setValue("Анна-Мария Антонова");
        $("[name='phone']").setValue("+79099001122");
        $(".checkbox").click();
        $(".button__text").click();
        $("[data-test-id='date'] .input_invalid .input__sub").shouldHave(exactText("Неверно введена дата"));
    }

    @Test
    void shouldNotValidDate() {
        $("[placeholder='Город']").setValue("Мурманск");
        $("[placeholder='Дата встречи']").setValue(data(2));
        $("[name='name']").setValue("Анна-Мария Антонова");
        $("[name='phone']").setValue("+79099001122");
        $(".checkbox").click();
        $(".button__text").click();
        $("[data-test-id='date'] .input_invalid .input__sub").shouldHave(exactText("Заказ на выбранную дату невозможен"));
    }

    @Test
    void shouldEmptyName() {
        $("[placeholder='Город']").setValue("Мурманск");
        $("[placeholder='Дата встречи']").setValue(data(3));
        $("[name='phone']").setValue("+79099001122");
        $(".checkbox").click();
        $(".button__text").click();
        $("[data-test-id='name'].input_invalid .input__sub").shouldHave(exactText("Поле обязательно для заполнения"));
    }

    @Test
    void shouldNotValidName() {
        $("[placeholder='Город']").setValue("Мурманск");
        $("[placeholder='Дата встречи']").setValue(data(4));
        $("[name='name']").setValue("Anna");
        $("[name='phone']").setValue("+79099001122");
        $(".checkbox").click();
        $(".button__text").click();
        $("[data-test-id='name'].input_invalid .input__sub").shouldHave(exactText("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы."));
    }

    @Test
    void shouldEmptyPhone() {
        $("[placeholder='Город']").setValue("Мурманск");
        $("[placeholder='Дата встречи']").setValue(data(4));
        $("[name='name']").setValue("Анна-Мария Антонова");
        $(".checkbox").click();
        $(".button__text").click();
        $("[data-test-id='phone'].input_invalid .input__sub").shouldHave(exactText("Поле обязательно для заполнения"));
    }

    @Test
    void shouldNotValidPhone() {
        $("[placeholder='Город']").setValue("Мурманск");
        $("[placeholder='Дата встречи']").setValue(data(4));
        $("[name='name']").setValue("Анна-Мария Антонова");
        $("[name='phone']").setValue("79099001122");
        $(".checkbox").click();
        $(".button__text").click();
        $("[data-test-id='phone'].input_invalid .input__sub").shouldHave(exactText("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678."));
    }

    @Test
    void shouldNoCheckBox() {
        $("[placeholder='Город']").setValue("Мурманск");
        $("[placeholder='Дата встречи']").setValue(data(4));
        $("[name='name']").setValue("Анна-Мария Антонова");
        $("[name='phone']").setValue("+79099001122");
        $(".button__text").click();
        $("[data-test-id='agreement'].input_invalid").shouldHave(exactText("Я соглашаюсь с условиями обработки и использования моих персональных данных"));
    }

}
