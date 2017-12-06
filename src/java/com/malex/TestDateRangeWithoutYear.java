package com.malex;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertArrayEquals;

public class TestDateRangeWithoutYear
{

    private Set<FuzzyTimeDomainClazz> fuzzyTimeDomains;

    @Before
    public void before()
    {
        fuzzyTimeDomains = Stream.of(new FuzzyTimeDomainClazz(2, "SPRING", 21, 3, 20, 6),
                new FuzzyTimeDomainClazz(4, "AUTUMN", 21, 9, 20, 12),
                new FuzzyTimeDomainClazz(3, "SUMMER", 21, 6, 20, 9),
                new FuzzyTimeDomainClazz(1, "WINTER", 21, 12, 20, 3)).collect(Collectors.toSet());
    }

    @Test
    public void verifyPosition()
    {
        int num = 1;
        for (FuzzyTimeDomainClazz fuzzy : fuzzyTimeDomains)
        {
            assertEquals(num, fuzzy.getPosition());
            num++;
        }
    }

    @Test
    public void testForOneFuzzyTimeDomain()
    {
        // "AUTUMN"
        DateRange dateRange_middle_AUTUMN_1 = new DateRange(1, 10, 2015, 1, 11, 2015);
        DateRange dateRange_middle_AUTUMN_2 = new DateRange(31, 10, 2015, 31, 11, 2015);
        DateRange dateRange_start_AUTUMN_3 = new DateRange(21, 9, 2015, 31, 11, 2015);
        DateRange dateRange_end_AUTUMN_4 = new DateRange(31, 10, 2015, 20, 12, 2015);
        DateRange dateRange_start_end_AUTUMN_4 = new DateRange(21, 9, 2015, 20, 12, 2015);

        checkFuzzyTimeDomainList(getFuzzyTimeDomainClazzByDateRange(dateRange_middle_AUTUMN_1), 1, "AUTUMN");
        checkFuzzyTimeDomainList(getFuzzyTimeDomainClazzByDateRange(dateRange_middle_AUTUMN_2), 1, "AUTUMN");
        checkFuzzyTimeDomainList(getFuzzyTimeDomainClazzByDateRange(dateRange_start_AUTUMN_3), 1, "AUTUMN");
        checkFuzzyTimeDomainList(getFuzzyTimeDomainClazzByDateRange(dateRange_end_AUTUMN_4), 1, "AUTUMN");
        checkFuzzyTimeDomainList(getFuzzyTimeDomainClazzByDateRange(dateRange_start_end_AUTUMN_4), 1, "AUTUMN");

//    // "SUMMER" and "AUTUMN"
//    DateRange dateRange_SUMMER_AUTUMN_5 = new DateRange(20, 9, 2015, 20, 12, 2015);
//    checkFuzzyTimeDomainList(getFuzzyTimeDomainClazzByDateRange(dateRange_SUMMER_AUTUMN_5), 2, "AUTUMN", "SUMMER");

    }

    private void checkFuzzyTimeDomainList(List<FuzzyTimeDomainClazz> list, int actualSizeOfList, String... nameFuzzyTime)
    {
        List<String> fuzzyTimeNames = list.stream().map(FuzzyTimeDomainClazz::getFuzzy).collect(Collectors.toList());
        assertEquals(actualSizeOfList, list.size());
        assertTrue(fuzzyTimeNames.containsAll(Arrays.asList(nameFuzzyTime)));
    }

    private List<FuzzyTimeDomainClazz> getFuzzyTimeDomainClazzByDateRange(DateRange dateRange)
    {
        // return Stream.of(new FuzzyTimeDomainClazz(4, "AUTUMN", 21, 9, 20, 12)).collect(Collectors.toList());
        return fuzzyTimeDomains.stream().filter(filterByMonth(dateRange)).filter(filterByDay(dateRange)).collect(Collectors.toList());
    }

    private Predicate<? super FuzzyTimeDomainClazz> filterByDay(DateRange dateRange)
    {
        return fuzzyTime ->
        {

            if (fuzzyTime.isLimitValue(dateRange.startDateMonth))
            {
                if (dateRange.startDateMonth == fuzzyTime.startDateMonth)
                {
                    boolean flag = dateRange.startDateDay >= fuzzyTime.startDateDay;
                    return flag;
                }
                return false;
            }

            if (fuzzyTime.isLimitValue(dateRange.getEndDateMonth()))
            {

                if (fuzzyTime.isLimitValue(dateRange.endDateMonth))
                {
                    if (dateRange.endDateMonth == fuzzyTime.endDateMonth)
                    {
                        boolean flag = dateRange.endDateDay <= fuzzyTime.endDateDay;
                        return flag;
                    }
                    return false;
                }
            }

            return true;
        };
    }

    private Predicate<FuzzyTimeDomainClazz> filterByMonth(DateRange dateRange)
    {
        return fuzzyTime -> fuzzyTime.isFound(dateRange.startDateMonth) || fuzzyTime.isFound(dateRange.getEndDateMonth());
    }

    @Getter
    @Setter
    @EqualsAndHashCode(exclude = {"fuzzy", "startDateDay", "startDateMonth", "endDateDay", "endDateMonth"})
    private static class FuzzyTimeDomainClazz
    {
        private int position;
        private String fuzzy;
        private int startDateDay;
        private int startDateMonth;
        private int endDateDay;
        private int endDateMonth;
        private int[] monthRage;
        private Map<Integer, List<Integer>> monthRageWithDays;

        FuzzyTimeDomainClazz(int position, String fuzzy, int startDateDay, int startDateMonth, int endDateDay, int endDateMonth)
        {
            this.position = position;
            this.fuzzy = fuzzy;
            this.startDateDay = startDateDay;
            this.startDateMonth = startDateMonth;
            this.endDateDay = endDateDay;
            this.endDateMonth = endDateMonth;
            this.monthRage = getMonthByRange(startDateMonth, endDateMonth);
            this.monthRageWithDays = createMap(this.monthRage, startDateDay, endDateDay); // todo <<< simply
        }

        public boolean isFound(int month)
        {
            return Arrays.stream(monthRage).anyMatch(num -> num == month);
        }

        public boolean isLimitValue(int month)
        {
            return monthRage[0] == month || monthRage[monthRage.length - 1] == month;
        }

        // TODO >>>> NEW API
        private Map<Integer, List<Integer>> createMap(int[] monthRage, int startDateDay, int endDateDay)
        {

//            Arrays.stream(monthRage)
//                    .map()

            return new HashMap<>();
        }

    }

    @Getter
    @Setter
    private static class DateRange
    {
        private int startDateDay;
        private int startDateMonth;
        private int startDateYear;

        private int endDateDay;
        private int endDateMonth;
        private int endDateYear;

        DateRange(int startDateDay, int startDateMonth, int startDateYear, int endDateDay, int endDateMonth, int endDateYear)
        {
            this.startDateDay = startDateDay;
            this.startDateMonth = startDateMonth;
            this.startDateYear = startDateYear;
            this.endDateDay = endDateDay;
            this.endDateMonth = endDateMonth;
            this.endDateYear = endDateYear;
        }
    }

    @Test
    public void test1()
    {
        assertArrayEquals(new int[]{3, 4, 5, 6}, getMonthByRange(3, 6));
        assertArrayEquals(new int[]{1, 2, 3, 4}, getMonthByRange(1, 4));
        assertArrayEquals(new int[]{9, 10, 11, 12}, getMonthByRange(9, 12));

        // 21.12 - 20.03.
        assertArrayEquals(new int[]{12, 1, 2, 3}, getMonthByRange(12, 3));
        // 21.03. - 20.06.
        assertArrayEquals(new int[]{3, 4, 5, 6}, getMonthByRange(3, 6));
        // 21.06. - 20.09.
        assertArrayEquals(new int[]{6, 7, 8, 9}, getMonthByRange(6, 9));
        // 21.09. - 20.12.
        assertArrayEquals(new int[]{9, 10, 11, 12}, getMonthByRange(9, 12));

        // 21.06. - 20.09.
        assertArrayEquals(new int[]{6, 7, 8, 9}, getMonthByRange(6, 9));
        // 21.09. - 20.12.
        assertArrayEquals(new int[]{9, 10, 11, 12}, getMonthByRange(9, 12));
        // 21.12. - 20.03.
        assertArrayEquals(new int[]{12, 1, 2, 3}, getMonthByRange(12, 3));
        // 21.03. - 20.06.
        assertArrayEquals(new int[]{3, 4, 5, 6}, getMonthByRange(3, 6));

        assertArrayEquals(new int[]{11, 12, 1, 2, 3}, getMonthByRange(11, 3));
        assertArrayEquals(new int[]{9, 10, 11, 12, 1}, getMonthByRange(9, 1));
        assertArrayEquals(new int[]{2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 1}, getMonthByRange(2, 1));
        assertArrayEquals(new int[]{12, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11}, getMonthByRange(12, 11));
    }

    private static final int DEFAULT_NUM = 0;
    private static final int FIRST_MONTH = 1;
    private static final int LAST_MONTH = 12;
    private static final int[] MONTH_RANGE_OF_YEAR = IntStream.rangeClosed(FIRST_MONTH, LAST_MONTH).toArray();

    private static int[] getMonthByRange(int start, int end)
    {
        int position = end - start;
        if (position > DEFAULT_NUM)
        {
            return Arrays.copyOfRange(MONTH_RANGE_OF_YEAR, start - FIRST_MONTH, end);
        } else
        {
            int size = LAST_MONTH + position + FIRST_MONTH;
            int[] temp = new int[size];
            if (start == LAST_MONTH)
            {
                temp[DEFAULT_NUM] = start;
                System.arraycopy(MONTH_RANGE_OF_YEAR, DEFAULT_NUM, temp, FIRST_MONTH, end);
                return temp;
            } else
            {
                int[] monthByRange = getMonthByRange(start, LAST_MONTH);
                System.arraycopy(monthByRange, DEFAULT_NUM, temp, DEFAULT_NUM, monthByRange.length);
                System.arraycopy(MONTH_RANGE_OF_YEAR, DEFAULT_NUM, temp, monthByRange.length, end);
                return temp;
            }
        }
    }
}
