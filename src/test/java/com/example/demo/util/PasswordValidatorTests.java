package com.example.demo.util;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class PasswordValidatorTests {

    private PasswordValidator passwordValidator;

    @BeforeEach
    public void beforeEach() {
        this.passwordValidator = PasswordValidatorFactory.create();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "12#Fourfiv3sixSEVEN",
            "aaaaaaa*E6",
            "AAAAAAAb!9",
            "8932984Bc(",
            "(*&$#I*&1Qw"
    })
    public void passwordIsValid(String password) {
        assertTrue(passwordValidator.validate(password, password),
                passwordValidator.getReasonMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            " ",
            "asdf",
            "123123123123",
            "asdfasdfasdf",
            "PPOPOUEINDOOE",
            "*#*#(*$(*#($#"
    })
    public void passwordIsNotValid(String password) {
        assertFalse(passwordValidator.validate(password, password),
                passwordValidator.getReasonMessage());
    }

    @Test
    public void passwordIsTooShort() {
        String password = "tOo*sh0t";
        assertFalse(passwordValidator.validate(password, password));
        assertEquals(passwordValidator.getReasonMessage(), PasswordValidator.REASON_MIN_LENGTH);
    }

    @Test
    public void passwordHasNoDigits() {
        String password = "EveryThing@#$%";
        assertFalse(passwordValidator.validate(password, password));
        assertEquals(passwordValidator.getReasonMessage(), PasswordValidator.REASON_MIN_DIGITS);
    }

    @Test
    public void passwordHasNoUppercase() {
        String password = "thisisalllower*4";
        assertFalse(passwordValidator.validate(password, password));
        assertEquals(passwordValidator.getReasonMessage(), PasswordValidator.REASON_MIN_UPPERCASE);
    }

    @Test
    public void passwordHasNoLowercase() {
        String password = "ASEVDIR3(A)32";
        assertFalse(passwordValidator.validate(password, password));
        assertEquals(passwordValidator.getReasonMessage(), PasswordValidator.REASON_MIN_LOWERCASE);
    }

    @Test
    public void passwordHasNoSpecialCharacter() {
        String password = "ASEVDIR3asA32";
        assertFalse(passwordValidator.validate(password, password));
        assertEquals(passwordValidator.getReasonMessage(), PasswordValidator.REASON_MIN_SPECIAL_CHARS);
    }

    @Test
    public void passwordsDoNotMatch() {
        String password = "*AVeryFin3Password";
        String confirm = "thisisdi$4Bfferent";
        assertFalse(passwordValidator.validate(password, confirm));
        assertEquals(passwordValidator.getReasonMessage(), PasswordValidator.REASON_NO_MATCH);
    }
}
