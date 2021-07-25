package cu.students.entities;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import cu.students.EnrollUtils;

public final class StudentEnrollment {
	
	private final String studentId;
	private final String department;
	private final String course;
	private final int applStatus;
	private final Date applDate;
	
	public StudentEnrollment(StudentEnrollmentBuilder builder) {
		this.studentId = builder.studentId;
		this.department = builder.department;
		this.course = builder.course;
		this.applStatus = builder.applStatus;
		this.applDate = builder.applDate;
	}

	@JsonProperty("StudentID")
	public String getStudentId() {
		return studentId;
	}
	
	@JsonProperty("Department")
	public String getDepartment() {
		return department;
	}
	
	@JsonProperty("Course")
	public String getCourse() {
		return course;
	}

	@JsonProperty("Status")
	public int getApplStatus() {
		return applStatus;
	}

	@JsonProperty("Timestamp")
	public Date getApplDate() {
		return applDate;
	}
	
	@Override
    public String toString() {
        return "Student ID: "+this.studentId+", Department: "+this.department+", Course: "+this.course+ ", Application Status: "+this.studentId+", Application Date: "
        		+EnrollUtils.getDateString(this.applDate);
    }
	
	public static class StudentEnrollmentBuilder {

		private final String studentId;
		private String department;
		private String course;
		private int applStatus;
		private Date applDate;

        public StudentEnrollmentBuilder(String studentId) {
            this.studentId	= studentId;
        }

        public StudentEnrollmentBuilder setDepartment(String department){
            this.department = department;
            return this;
        }

        public StudentEnrollmentBuilder setCourse(String course){
            this.course = course;
            return this;
        }

        public StudentEnrollmentBuilder setApplStatus(int applStatus){
            this.applStatus = applStatus;
            return this;
        }

        public StudentEnrollmentBuilder setApplDate(Date applDate){
            this.applDate = applDate;
            return this;
        }

        public StudentEnrollment build(){
        	StudentEnrollment studentEnrollment = new StudentEnrollment(this);
            return studentEnrollment;
        }
    }
}
