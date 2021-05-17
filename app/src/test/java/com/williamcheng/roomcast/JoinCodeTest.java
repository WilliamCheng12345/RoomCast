package com.williamcheng.roomcast;

import com.williamcheng.roomcast.classes.Roommates;

import static org.junit.Assert.*;
import org.junit.Test;

public class JoinCodeTest {
    @Test
    public void testJoinCode() {
        for(int i = 0; i < 9999;i ++) {
            Roommates testRoommates = new Roommates("test");
            System.out.println(testRoommates.getJoinCode());
            assertTrue(testRoommates.getJoinCode().matches("[a-zA-Z0-9]+"));
        }
    }
}
