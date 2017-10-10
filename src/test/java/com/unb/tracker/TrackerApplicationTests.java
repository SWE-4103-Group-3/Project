package com.unb.tracker;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TrackerApplicationTests {

    @Autowired
	private TrackerController controller;
    @Autowired
    private MockMvc mockMvc;

    @Test
	public void contextLoads() throws Exception {
		assertThat(controller).isNotNull();
	}

    @Test
    public void shouldReturnIndex() throws Exception {
        this.mockMvc.perform(get("/")).andDo(print()).andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    public void shouldHit404() throws Exception {
        this.mockMvc.perform(get("/thisdoesnotexist")).andDo(print()).andExpect(status().is(404));
    }

}
