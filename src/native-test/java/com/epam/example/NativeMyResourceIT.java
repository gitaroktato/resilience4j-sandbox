package com.epam.example;

import io.quarkus.test.junit.NativeImageTest;

@NativeImageTest
public class NativeMyResourceIT extends MyResourceTest {

    // Execute the same tests but in native mode.
}