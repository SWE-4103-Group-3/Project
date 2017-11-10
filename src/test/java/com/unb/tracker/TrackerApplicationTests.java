package com.unb.tracker;

import com.unb.tracker.model.Course;
import com.unb.tracker.model.Seat;
import com.unb.tracker.model.User;
import com.unb.tracker.repository.CourseRepository;
import com.unb.tracker.repository.UserRepository;
import com.unb.tracker.web.CourseController;
import com.unb.tracker.web.UserController;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.in;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(SpringRunner.class)
@SpringBootTest
public class TrackerApplicationTests {
    protected final DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    @Autowired
    private CourseController courseController;

    @Autowired
    private UserController userController;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockBean
    private CourseRepository courseRepository;

    @MockBean
    private UserRepository userRepository;

    //TODO:
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
    //- Test that a student is not able to create courses

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {

        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream()
                .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
                .findAny()
                .orElse(null);

        assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);
    }

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void contextLoads() throws Exception {
        assertThat(courseController).isNotNull();
        assertThat(userController).isNotNull();
    }


    @Test
    public void shouldReturnIndex() throws Exception {
        this.mockMvc
                .perform(get("/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    public void shouldBeRedirected() throws Exception {
        this.mockMvc.perform(get("/thisdoesnotexist"))
                .andDo(print())
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("http://localhost/")); // redirected
    }

    @Test
    @WithMockUser("test")
    public void saveCourse() throws Exception {
        User instructor = new User();
        instructor.setUsername("test");
        instructor.setHasExtendedPrivileges(true);
        when(userRepository.findByUsername("test")).thenReturn(instructor);

        when(courseRepository.save(Matchers.anyCollection())).then(returnsFirstArg());

        String name = "DGDUSMMYVK";
        String timeSlot = "8:30";
        String startDate = "2017-01-01";
        String endDate = "2017-01-01";
        String section = "section";
        Integer rows = 10;
        Integer cols = 11;
        Course myCourse = new Course();
        myCourse.setName(name);
        myCourse.setRows(rows);
        myCourse.setCols(cols);
        myCourse.setSection(section);
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
                .param("rows", rows.toString())
                .param("section", section))
                .andDo(print())
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/test/DGDUSMMYVK/section")); //redirected
    }

    @Test
    public void testSignup() throws Exception {
        when(userRepository.save(Matchers.anyCollection())).then(returnsFirstArg());

        this.mockMvc.perform(post("/signup")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("email", "test@unb.ca")
                .param("username", "name")
                .param("password", "password")
                .param("passwordConfirm", "password")
                .param("hasExtendedPrivileges", "false"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser("test")
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
                .andExpect(status().isBadRequest());

    }

    @Test
    @WithMockUser("test")
    public void coursePage() throws Exception {
        User instructor = new User();
        instructor.setUsername("test");
        Course course = new Course();
        course.setInstructor(instructor);
        course.setName("test");

        when(userRepository.findByUsername("test")).thenReturn(new User());

        when(courseRepository.findByInstructorUsernameAndName("test", "test")).thenReturn(new ArrayList<Course>() {{
            add(course);
        }});

        mockMvc.perform(get("/test/test"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser("test")
    public void coursePageWithSection() throws Exception {
        User instructor = new User();
        instructor.setUsername("test");
        Course course = new Course();
        course.setInstructor(instructor);
        course.setSection("test");
        course.setName("test");
        when(courseRepository.findByInstructorUsernameAndNameAndSection("test", "test", "test")).thenReturn(new ArrayList<Course>() {{
            add(course);
        }});

        when(userRepository.findByUsername("test")).thenReturn(instructor);

        mockMvc.perform(get("/test/test/test"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser("test")
    public void loginRedirect() throws Exception {
        mockMvc.perform(get("/login/success"))
                .andDo(print())
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/test"));
    }

    @Test
    @WithMockUser
    public void getCourses() throws Exception {
        when(courseRepository.findAll()).thenReturn(null);
        mockMvc.perform(get("/courses"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser("test")
    public void getCourseThatExists() throws Exception {
        Course course = new Course();
        course.setId(1l);
        when(courseRepository.findOne(1l)).thenReturn(course);
        mockMvc.perform(get("/courses/1"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser("test")
    public void getCourseThatDoesNotExists() throws Exception {
        Course course = new Course();
        course.setId(1l);
        when(courseRepository.findOne(1l)).thenReturn(null);
        mockMvc.perform(get("/courses/1"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser("test")
    public void postSeat() throws Exception {
        Course course = new Course();
        course.setId(1l);
        when(courseRepository.findOne(1l)).thenReturn(course);

        User user = new User();
        user.setId(1);
        when(userRepository.findByUsername("test")).thenReturn(user);

        Seat seat = new Seat();
        seat.setId(1l);
        seat.setCourse(course);
        seat.setStudent(user);
        seat.setCol(7);

        Seat s = new Seat();
        s.setId(1l);

        List<Seat> seats = new ArrayList<>();
        seats.add(s);
        course.setSeats(seats);

        mockMvc.perform(get("/courses/1/seat")
                .content(this.json(seat)))
                .andDo(print())
                .andExpect(status().isOk());
                //.andExpect(content().string("saved"));

    }

    /*@Test
    public void testLogin() throws Exception {
        mockMvc.perform(formLogin("/").user("jsmith21").password("jsmith21jsmith21"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(redirectedUrl("/jsmith21"));
    }*/

    @Test
    @WithMockUser
    public void testLogout() throws Exception {
        mockMvc.perform(logout())
                .andDo(print())
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/"));
    }

    private Date convertToSqlDate(String dateString) throws Exception {
        return new java.sql.Date(dateFormat.parse(dateString).getTime());
    }

    protected String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

}
