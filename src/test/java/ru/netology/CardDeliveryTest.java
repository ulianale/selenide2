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

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class CardDeliveryTest {

    public String data(int days) {  // метод прибавляет заданное кол-во дней к текущей дате, и возвращает её в нужном формате
        LocalDate date = LocalDate.now().plusDays(days);
        return date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    public void twoLetters(String letters) { // выбор города из выпадающего списка по двум буквам, если буквы не совпадают -> исключение
        $("[placeholder='Город']").setValue(letters);
        ElementsCollection forms = $$(".menu-item__control");
        int x = 0; // счетчик
        for (SelenideElement form : forms) {
            if (letters.equalsIgnoreCase(form.getText().substring(0, 2))) {
                x++;
                form.click();
                break;
            }
        }
        if (x < 1) {
            throw new ElementClickInterceptedException("Упс");
        }
    }

    public void dayInCalendar(String data) {  // метод выбора дня в календаре(в календарном месяце)
        ElementsCollection forms = $$(".calendar__day");
        for (SelenideElement form : forms) {
            if (data.equals(form.getText())) {
                form.click();
                break;
            }
        }
    }

    public void dayInOneWeek() {   // выбор даты на НЕДЕЛЮ вперед через календарь
        int today = LocalDate.now().getDayOfMonth();
        int dayInOneWeek = LocalDate.now().plusDays(7).getDayOfMonth(); // дата которую нужно поставить в календаре
        String data = "" + dayInOneWeek; // преобразую число в строку

        $("[placeholder='Дата встречи'").click();
        if (dayInOneWeek > today) {
            dayInCalendar(data);
        } else {
            $$(".calendar__arrow").last().click();
            dayInCalendar(data);
        }

    }

    @BeforeEach
    void setUp() {
        //Configuration.holdBrowserOpen = true;
        open("http://localhost:9999");
    }

    @Test
    void shouldAutoCompleteCityAndUseCalendar() {
        twoLetters("ор");
        dayInOneWeek();
        $("[name='name']").setValue("Анна-Мария Антонова");
        $("[name='phone']").setValue("+79099001122");
        $(".checkbox").click();
        $(".button__text").click();
        $("[data-test-id='notification']").shouldBe(exactText("Успешно! Встреча успешно забронирована на " + data(7)), Duration.ofSeconds(15));
    }

    @Test
    void shouldExceptionIfNotTwoLetters() {
        Assertions.assertThrows(ElementClickInterceptedException.class, () -> twoLetters("аа"));
    }

    @Test
    void shouldValidForm() {
        $("[placeholder='Город']").setValue("Мурманск");
        $("[placeholder='Дата встречи']").doubleClick().sendKeys(Keys.DELETE);
        $("[placeholder='Дата встречи']").setValue(data(3));
        $("[name='name']").setValue("Анна-Мария Антонова");
        $("[name='phone']").setValue("+79099001122");
        $(".checkbox").click();
        $(".button__text").click();
        $("[data-test-id='notification']").shouldBe(exactText("Успешно! Встреча успешно забронирована на " + data(3)), Duration.ofSeconds(15));
    }

    @Test
    void shouldEmptyCity() {
        $("[placeholder='Дата встречи']").doubleClick().sendKeys(Keys.DELETE);
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
        $("[placeholder='Дата встречи']").doubleClick().sendKeys(Keys.DELETE);
        $("[placeholder='Дата встречи']").setValue(data(20));
        $("[name='name']").setValue("Анна-Мария Антонова");
        $("[name='phone']").setValue("+79099001122");
        $(".checkbox").click();
        $(".button__text").click();
        $("[data-test-id='city'].input_invalid .input__sub").shouldHave(exactText("Доставка в выбранный город недоступна"));
    }

    @Test
    void shouldEmptyData() {
        $("[placeholder='Город']").setValue("Мурманск");
        $("[placeholder='Дата встречи']").doubleClick().sendKeys(Keys.DELETE);
        $("[name='name']").setValue("Анна-Мария Антонова");
        $("[name='phone']").setValue("+79099001122");
        $(".checkbox").click();
        $(".button__text").click();
        $("[data-test-id='date'] .input_invalid .input__sub").shouldHave(exactText("Неверно введена дата"));
    }

    @Test
    void shouldNotValidData() {
        $("[placeholder='Город']").setValue("Мурманск");
        $("[placeholder='Дата встречи']").doubleClick().sendKeys(Keys.DELETE);
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
        $("[placeholder='Дата встречи']").doubleClick().sendKeys(Keys.DELETE);
        $("[placeholder='Дата встречи']").setValue(data(3));
        $("[name='phone']").setValue("+79099001122");
        $(".checkbox").click();
        $(".button__text").click();
        $("[data-test-id='name'].input_invalid .input__sub").shouldHave(exactText("Поле обязательно для заполнения"));
    }

    @Test
    void shouldNotValidName() {
        $("[placeholder='Город']").setValue("Мурманск");
        $("[placeholder='Дата встречи']").doubleClick().sendKeys(Keys.DELETE);
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
        $("[placeholder='Дата встречи']").doubleClick().sendKeys(Keys.DELETE);
        $("[placeholder='Дата встречи']").setValue(data(4));
        $("[name='name']").setValue("Анна-Мария Антонова");
        $(".checkbox").click();
        $(".button__text").click();
        $("[data-test-id='phone'].input_invalid .input__sub").shouldHave(exactText("Поле обязательно для заполнения"));
    }

    @Test
    void shouldNotValidPhone() {
        $("[placeholder='Город']").setValue("Мурманск");
        $("[placeholder='Дата встречи']").doubleClick().sendKeys(Keys.DELETE);
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
        $("[placeholder='Дата встречи']").doubleClick().sendKeys(Keys.DELETE);
        $("[placeholder='Дата встречи']").setValue(data(4));
        $("[name='name']").setValue("Анна-Мария Антонова");
        $("[name='phone']").setValue("+79099001122");
        $(".button__text").click();
        $("[data-test-id='agreement'].input_invalid").shouldHave(exactText("Я соглашаюсь с условиями обработки и использования моих персональных данных"));
    }

}
