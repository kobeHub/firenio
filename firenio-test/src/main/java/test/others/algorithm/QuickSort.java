/*
 * Copyright 2015 The FireNio Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package test.others.algorithm;

import java.util.Random;

import com.firenio.common.Util;
import com.firenio.log.LoggerFactory;

/**
 * @author wangkai
 */
public class QuickSort {

    public static void main(String[] args) {

        TestSort.test(QuickSort::sort, 10);
                TestSort.test_load(QuickSort::sort, 1024 * 32, 1024);
//        TestSort.test_load(QuickSort::sort, 16, 1024 * 1024 * 8);

    }

    private static int partition(int[] array, int low, int high) {
        int mid = low + (high - low) / 2;
        if (array[mid] > array[high]) {
            swap(array, mid, high);
        }
        if (array[low] > array[high]) {
            swap(array, low, high);
        }
        if (array[mid] > array[low]) {
            swap(array, mid, low);
        }

        int key = array[low];

        while (low < high) {
            while (array[high] >= key && high > low) {
                high--;
            }
            array[low] = array[high];
            while (array[low] <= key && high > low) {
                low++;
            }
            array[high] = array[low];
        }
        array[low] = key;
        return high;
    }

    public static void sort(int[] array) {
        sort(array, 0, array.length - 1);
    }

    public static void sort(int[] array, int low, int high) {
        if (low >= high) {
            return;
        }
        int index = partition(array, low, high);
        sort(array, low, index - 1);
        sort(array, index + 1, high);
    }

    private static void swap(int[] array, int a, int b) {
        int temp = array[a];
        array[a] = array[b];
        array[b] = temp;
    }

}
