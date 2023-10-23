package com.example.demo.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *  Password Validation
 *
 *  Complexity requirements:
 *
 1. The password may not contain the account name or variations on the account name.

 (Must have all of the following)
 2. It must contain characters from three of the following five groups (quoted from the Microsoft document):
 3. Uppercase letters of European languages (A through Z, with diacritical marks, Greek and Cyrillic characters)
 4. Lowercase letters of European languages (A through Z, sharp S, with diacritical marks, Greek and Cyrillic characters)
 5. Base 10 digits (0 through 9);
 6. non-alphanumeric characters (special characters): (~!@#$%^&*_-+=`|\(){}[]:;"'<>,.?/)
 */

public class PasswordValidatorFactory {
    public static PasswordValidator create() {
        return new PasswordValidator();
    }
}
