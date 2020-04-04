package io.ash.simpletoe;

import org.junit.Test;

import io.ash.simpletoe.enums.Figures;

import static org.junit.Assert.*;

public class ConfigurationStashTest {
    @Test
    public void setFigure() {
        ConfigurationStash.uId = "1";

        ConfigurationStash.setFigure("1", "");
        assertEquals(ConfigurationStash.figure, Figures.CROSS);

        ConfigurationStash.figure = Figures.EMPTY;
        ConfigurationStash.setFigure("", "1");
        assertEquals(ConfigurationStash.figure, Figures.NOUGHT);

        ConfigurationStash.figure = Figures.EMPTY;
        ConfigurationStash.setFigure("", "");
        assertEquals(ConfigurationStash.figure, Figures.EMPTY);
    }

    @Test
    public void setError() {
        ConfigurationStash.uId = "1";

        ConfigurationStash.setFigure("2", "3");
        assertEquals(ConfigurationStash.figure, Figures.EMPTY);
    }
}