import org.junit.Assert;
import org.junit.Test;
import tn.esprit.R2S.interfaces.ICandidateService;
import tn.esprit.R2S.interfaces.IJobService;
import tn.esprit.R2S.interfaces.ISkillService;
import tn.esprit.R2S.interfaces.IUsersService;
import tn.esprit.R2S.model.*;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Date;
import java.util.List;

/**
 * Created by evilkid on 11/1/2016.
 */
public class YassineTestCase {

    private final String BASE = "/tn.esprit.R2S-ear/tn.esprit.R2S-ejb/";
    private final String PACKAGE_NAME = "!tn.esprit.R2S.interfaces.";
    private Context context;
    private IUsersService usersService;
    private ISkillService skillService;
    private ICandidateService candidateService;
    private IJobService jobService;

    public YassineTestCase() throws NamingException {
        context = new InitialContext();
    }

    public IUsersService getUsersServiceProxy() {
        if (usersService == null) {
            try {
                usersService = (IUsersService) context.lookup(BASE + "UsersService" + PACKAGE_NAME + "IUsersService");
            } catch (NamingException e) {
                e.printStackTrace();
                System.out.println("Couldnt get EJB");
            }
        }
        return usersService;
    }

    public ISkillService getSkillServiceProxy() {
        if (skillService == null) {
            try {
                skillService = (ISkillService) context.lookup(BASE + "SkillService" + PACKAGE_NAME + "ISkillService");
            } catch (NamingException e) {
                e.printStackTrace();
                System.out.println("Couldnt get EJB");
            }
        }
        return skillService;
    }

    public ICandidateService getCandidateService() {
        if (candidateService == null) {
            try {

                candidateService = (ICandidateService) context.lookup(BASE + "CandidateService" + PACKAGE_NAME + "ICandidateService");
            } catch (NamingException e) {
                e.printStackTrace();
                System.out.println("Couldnt get EJB");
            }
        }
        return candidateService;
    }


    public IJobService getJobService() {
        if (jobService == null) {
            try {

                jobService = (IJobService) context.lookup(BASE + "JobService" + PACKAGE_NAME + "IJobService");
            } catch (NamingException e) {
                e.printStackTrace();
                System.out.println("Couldnt get EJB");
            }
        }
        return jobService;
    }

    //HS-23 As a User, I want to authenticate
    @Test
    public void authenticate() {
        Users users = getUsersServiceProxy().login("cand", "cand");

        System.out.println("user retrieved: " + users);

        Assert.assertNotNull(users);
    }

    //HS-24 As a Chief Human Resources Officer, I want to Add a user
    @Test
    public void addUser() {

        try {
            Employee newEmp = new Employee();
            newEmp.setCin(9L);
            newEmp.setFirstname("new Employee");
            newEmp.setLastname("lastname");
            newEmp.setCredibility(0);
            newEmp.setBirthday(new Date());

            getUsersServiceProxy().create(newEmp);
            System.out.println("employee added");

            RecruitmentManager newRM = new RecruitmentManager();
            newRM.setCin(10L);
            newRM.setFirstname("new RM");
            newRM.setLastname("lastname");
            newRM.setBirthday(new Date());
            System.out.println("RM added");
        } catch (Exception e) {
            System.out.println("Adding failed due: " + e.getMessage());
            Assert.fail(e.getMessage());
        }

    }

    //HS-25 As a Chief Human Resources Officer, I want to edit a user
    @Test
    public void getAndEditUser() {
        Users users = getUsersServiceProxy().find(1L);
        if (users == null) {
            Assert.fail("user is null");
        }

        users.setLastname("edited!");
        users = getUsersServiceProxy().edit(users);
        System.out.println("user edited");

        Assert.assertNotNull(users);
        Assert.assertEquals("edited!", users.getLastname());
    }

    //HS-26 As a Chief Human Resources Officer, I want to disable a user
    @Test
    public void disableUser() {
        getUsersServiceProxy().disable(1L);

        System.out.println("User disabled");

        Users disabledUser = getUsersServiceProxy().find(1L);

        Assert.assertEquals(disabledUser.getActive(), false);
    }

    //HS-27 As a Chief Human Resources Officer, I want to List all users
    @Test
    public void findAllUsers() {
        List<Users> users = getUsersServiceProxy().findAll();

        System.out.println("users list: " + users);
        Assert.assertNotNull(users);
    }

    //HS-66 As a Chief Human Resources Officer, I want to view all skills
    @Test
    public void findAllSkills() {
        List<Skill> skills = getSkillServiceProxy().findAll();

        System.out.println("Skills list: " + skills);
        Assert.assertNotNull(skills);
    }

    //HS-67 As a Chief Human Resources Officer, I want to add a skill
    @Test
    public void addSkill() {
        Skill skill = new Skill();
        skill.setName("JUnit");

        getSkillServiceProxy().create(skill);
        System.out.println("skill added");
    }

    //HS-68 As a Chief Human Resources Officer, I want to delete a skill
    @Test
    public void deleteSkill() {
        Skill skill = getSkillServiceProxy().find(6L);

        getSkillServiceProxy().remove(skill);

        System.out.println("Skill removed");
    }

    //HS-69 As a Chief Human Resources Officer, I want to edit a skill
    @Test
    public void editASkill() {
        Skill skill = getSkillServiceProxy().find(7L);

        skill.setName("EntityFramework");

        skill = getSkillServiceProxy().edit(skill);

        System.out.println("Skill edited");

        Assert.assertNotNull(skill);
        Assert.assertEquals("EntityFramework", skill.getName());
    }

    //HS-83 As a Recruitment Manager I want to find candidate by a skill
    @Test
    public void findCandidateBySkill() {
        Skill skill = getSkillServiceProxy().find(1L);
        List<Candidate> candidates = getCandidateService().findBySkillId(skill.getId());

        System.out.println("candidates with skill " + skill.getName() + ": " + candidates);
    }

    //HS-91 As a Chief Human Resources Officer, I want to find users by role
    //REST
    public void findUsersByRole() {
        /*switch (role) {
            case CANDIDATE:
                return candidateService.findAll();
            case EMPLOYEE:
                return employeeService.findAll();
            case RECRUITMENT_MANAGER:
                return employeeService.findAll();
        }*/
    }

    //HS-106 As a Recruitment Manager, I want to find candidates which have the same job skills
    @Test
    public void matchCandidatesByJobSkills() {
        Job job = getJobService().find(1L);
        List<Candidate> candidates = getJobService().findCandidates(job.getId());

        System.out.println(candidates);

        Assert.assertNotNull(candidates);
    }

}
