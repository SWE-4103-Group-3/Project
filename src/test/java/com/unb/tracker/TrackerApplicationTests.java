package com.unb.tracker;

import com.google.common.collect.Iterables;
import com.unb.tracker.model.Course;
import com.unb.tracker.repository.CourseRepository;
import com.unb.tracker.web.CourseController;
import com.unb.tracker.web.UserController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TrackerApplicationTests {
    protected final DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

    @Autowired
	private CourseController courseController;
    @Autowired
    private UserController userController;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private CourseRepository courseRepository;

    //Things to test:
            //- Loading the index(login) should render
            //- Selecting one of the buttons should render the login card
            //- Selecting sign up should render the sign up card
            //- logging in with invalid credentials should redirect to index with error message
            //- attempting to create an account without an email address, username or password should redirect to index with error message
            //- test username and password validation (unique username with 6 to 32 length, matching passwords with length 8 to 32)
            //- Creating a new user should correctly store their information in the database
            //- Creating a new student should redirect to their student view
            //- Creating a new instructor should redirect to their instructor view
            //- Attempting to login as instructor or user should redirect them correctly

    @Test
	public void contextLoads() throws Exception {
		assertThat(courseController).isNotNull();
        assertThat(userController).isNotNull();
	}

    @Test
    public void shouldReturnIndex() throws Exception {
        this.mockMvc.perform(get("/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    public void shouldHit404() throws Exception {
        this.mockMvc.perform(get("/thisdoesnotexist"))
                .andDo(print())
                .andExpect(status().is(404));
    }

    @Test
    public void loadCourseFormView() throws Exception {
        this.mockMvc.perform(get("/course"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("courseFormView"));
    }
    @Test
    public void saveCourse() throws Exception {
        // Random name to be safe when retrieving
        String name = "DGDUSMMYVK";
        String timeSlot = "8:30";
        String startDate = "2017-01-01";
        String endDate = "2017-01-01";
        Integer rows = 10;
        Integer cols = 11;
        Course myCourse = new Course();
        myCourse.setName(name);
        myCourse.setRows(rows);
        myCourse.setCols(cols);

        myCourse.setStartDate(convertToSqlDate(startDate));
        myCourse.setEndDate(convertToSqlDate(endDate));
        myCourse.setTimeSlot(timeSlot);
        this.mockMvc.perform(post("/course")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("name", name)
                .param("timeSlot", timeSlot)
                .param("startDate", startDate)
                .param("endDate", endDate)
                .param("cols", cols.toString())
                .param("rows", rows.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("instructor/course"));

        Iterable<Course> courses = courseRepository.findAll();
        // TODO: Refactor this into something more sustainable and less silly
        assertThat(Arrays.stream(Iterables.toArray(courses, Course.class)).filter(course -> course.getName().equals(myCourse.getName())).toArray().length).isEqualTo(1);
    }

    private Date convertToSqlDate(String dateString) throws Exception {
            return new java.sql.Date(dateFormat.parse(dateString).getTime());
    }

    @Test
    public void hitCourseValidation() throws Exception {
        String name = "name";
        String timeSlot = "8:30";
        String startDate = "this isn't a real date";
        String endDate = "2017-01-01";
        //Dummy placeholder string to solve ambiguous argument problem
        String s = null;
        this.mockMvc.perform(post("/course")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("name", name)
                .param("timeSlot", timeSlot)
                .param("startDate", startDate)
                .param("endDate", endDate))
                .andDo(print())
                .andExpect(status().is(400));
                //TODO: Add this back once we have validation setup correctly
                //.andExpect(model().attributeHasErrors("startDate"));

    }

}
