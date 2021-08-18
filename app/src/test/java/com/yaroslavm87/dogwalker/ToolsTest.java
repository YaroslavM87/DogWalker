package com.yaroslavm87.dogwalker;

import com.yaroslavm87.dogwalker.viewModel.Tools;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ToolsTest {

//
//    @Before
//    public void

    private final long ONE_DAY_PERIOD = 86_400_000L;
    private final long THIRTY_DAYS_PERIOD = 2_592_000_000L;
    private final long COMMON_YEAR_PERIOD = 31_536_000_000L;
    private final long LEAP_YEAR_PERIOD = 31_622_400_000L;
    private final long FOUR_YEARS_PERIOD = 126_230_400_000L;
    private final long FORTY_YEARS_PERIOD = 316_224_010_950L;



    @Test
    public void isMomentInLeapYear_correct() {
        assertTrue(Tools.isMomentInLeapY(FORTY_YEARS_PERIOD + COMMON_YEAR_PERIOD + COMMON_YEAR_PERIOD - ONE_DAY_PERIOD));
    }

}