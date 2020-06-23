package com.trofimenko.loom;


import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SimpleTest {

    @Test
    public void Test(){
        int x = 9;
        int y = 10;

        assertEquals(19, x + y);
        assertEquals(90, x * y);


    }

    @RepeatedTest(5)
    void repeatedTest() {
        System.out.println("Этот тест будет запущен пять раз. ");
    }
}
