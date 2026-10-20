package com.employee360.backend.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.employee360.backend.entities.Department;
import com.employee360.backend.entities.DepartmentTopic;
import com.employee360.backend.entities.TopicSlot;
import com.employee360.backend.entities.TopicSlotLevel;
import com.employee360.backend.enums.QuizDifficulty;
import com.employee360.backend.repos.DepartmentRepo;
import com.employee360.backend.repos.DepartmentTopicRepo;
import com.employee360.backend.repos.TopicSlotLevelRepo;
import com.employee360.backend.repos.TopicSlotRepo;

@Component
public class LearningCatalogSeeder implements ApplicationRunner {

    private static final String MARKETING_DESCRIPTION =
            "Plans campaigns, builds the brand, understands audiences, and measures market performance.";
    private static final String FINANCE_DESCRIPTION =
            "Manages budgets, expenses, salaries, and financial records.";
    private static final List<QuizDifficulty> DIFFICULTIES = List.of(
            QuizDifficulty.BASIC,
            QuizDifficulty.MEDIUM,
            QuizDifficulty.HARD,
            QuizDifficulty.DIFFICULT,
            QuizDifficulty.EXPERT);

    private final DepartmentRepo departmentRepo;
    private final DepartmentTopicRepo topicRepo;
    private final TopicSlotRepo slotRepo;
    private final TopicSlotLevelRepo levelRepo;

    public LearningCatalogSeeder(
            DepartmentRepo departmentRepo,
            DepartmentTopicRepo topicRepo,
            TopicSlotRepo slotRepo,
            TopicSlotLevelRepo levelRepo) {
        this.departmentRepo = departmentRepo;
        this.topicRepo = topicRepo;
        this.slotRepo = slotRepo;
        this.levelRepo = levelRepo;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        Map<String, Department> departments = ensureDepartments();
        Map<TopicKey, DepartmentTopic> topics = ensureTopics(departments);
        Map<SlotKey, TopicSlot> slots = ensureSlots(topics);
        ensureLevels(slots);
    }

    private Map<String, Department> ensureDepartments() {
        List<Department> existing = departmentRepo.findAll();
        Map<String, Department> departments = new HashMap<>();

        for (DepartmentDefinition definition : definitions()) {
            Department department = existing.stream()
                    .filter(candidate -> definition.aliases().stream()
                            .anyMatch(alias -> normalizedName(alias)
                                    .equals(normalizedName(candidate.getName()))))
                    .findFirst()
                    .orElseGet(() -> {
                        Department created = new Department();
                        created.setName(definition.name());
                        created.setDescription(definition.description());
                        return departmentRepo.save(created);
                    });

            if (definition.name().equals("Marketing")
                    && (department.getDescription() == null
                            || department.getDescription().isBlank()
                            || FINANCE_DESCRIPTION.equalsIgnoreCase(
                                    department.getDescription().trim()))) {
                department.setDescription(MARKETING_DESCRIPTION);
                department = departmentRepo.save(department);
            }

            departments.put(definition.key(), department);
        }

        return departments;
    }

    private Map<TopicKey, DepartmentTopic> ensureTopics(
            Map<String, Department> departments) {
        Map<TopicKey, DepartmentTopic> topics = new HashMap<>();
        for (DepartmentTopic topic : topicRepo.findAll()) {
            topics.put(
                    new TopicKey(topic.getDepartment().getId(), topic.getTopicOrder()),
                    topic);
        }

        List<DepartmentTopic> missing = new ArrayList<>();
        for (DepartmentDefinition departmentDefinition : definitions()) {
            Department department = departments.get(departmentDefinition.key());
            for (int index = 0; index < departmentDefinition.topics().size(); index++) {
                TopicKey key = new TopicKey(department.getId(), index + 1);
                if (!topics.containsKey(key)) {
                    TopicSeed seed = departmentDefinition.topics().get(index);
                    missing.add(new DepartmentTopic(
                            department,
                            seed.title(),
                            seed.outline(),
                            index + 1));
                }
            }
        }

        if (!missing.isEmpty()) {
            topicRepo.saveAll(missing);
            topicRepo.flush();
        }
        for (DepartmentTopic topic : topicRepo.findAll()) {
            topics.putIfAbsent(
                    new TopicKey(topic.getDepartment().getId(), topic.getTopicOrder()),
                    topic);
        }
        return topics;
    }

    private Map<SlotKey, TopicSlot> ensureSlots(
            Map<TopicKey, DepartmentTopic> topics) {
        Map<SlotKey, TopicSlot> slots = new HashMap<>();
        for (TopicSlot slot : slotRepo.findAll()) {
            slots.put(new SlotKey(slot.getTopic().getId(), slot.getSlotNumber()), slot);
        }

        List<TopicSlot> missing = new ArrayList<>();
        for (DepartmentTopic topic : topics.values()) {
            for (int slotNumber = 1; slotNumber <= 10; slotNumber++) {
                SlotKey key = new SlotKey(topic.getId(), slotNumber);
                if (!slots.containsKey(key)) {
                    missing.add(new TopicSlot(
                            topic,
                            slotNumber,
                            String.format(Locale.ROOT, "Slot %02d", slotNumber)));
                }
            }
        }

        if (!missing.isEmpty()) {
            slotRepo.saveAll(missing);
            slotRepo.flush();
        }
        for (TopicSlot slot : slotRepo.findAll()) {
            slots.putIfAbsent(
                    new SlotKey(slot.getTopic().getId(), slot.getSlotNumber()),
                    slot);
        }
        return slots;
    }

    private void ensureLevels(Map<SlotKey, TopicSlot> slots) {
        Set<LevelKey> existing = new HashSet<>();
        for (TopicSlotLevel level : levelRepo.findAll()) {
            existing.add(new LevelKey(
                    level.getTopicSlot().getId(),
                    level.getDifficulty()));
        }

        List<TopicSlotLevel> missing = new ArrayList<>();
        for (TopicSlot slot : slots.values()) {
            for (QuizDifficulty difficulty : DIFFICULTIES) {
                LevelKey key = new LevelKey(slot.getId(), difficulty);
                if (!existing.contains(key)) {
                    missing.add(new TopicSlotLevel(slot, difficulty, 20));
                }
            }
        }
        if (!missing.isEmpty()) {
            levelRepo.saveAll(missing);
        }
    }

    private static String normalizedName(String value) {
        return value == null
                ? ""
                : value.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
    }

    private static List<DepartmentDefinition> definitions() {
        return List.of(
                new DepartmentDefinition(
                        "hr",
                        "Human Resources (HR)",
                        "Manages employee recruitment, attendance, leave, and employee records.",
                        List.of("Human Resources (HR)", "Human Resources", "HR"),
                        List.of(
                                topic("Recruitment and Selection", "Plan role requirements, source candidates, conduct structured interviews, and make fair hiring decisions."),
                                topic("Employee Onboarding", "Coordinate new-hire orientation, documentation, access, and early performance check-ins."),
                                topic("Attendance Management", "Maintain accurate attendance records, schedules, time reporting, and exception handling."),
                                topic("Leave Administration", "Apply leave policies consistently and manage requests, balances, approvals, and records."),
                                topic("Employee Records", "Maintain accurate, confidential employee files and follow retention and access rules."),
                                topic("Performance Management", "Set goals, conduct reviews, document feedback, and support fair performance decisions."),
                                topic("Compensation and Benefits", "Explain pay structures, benefits enrollment, eligibility, and compensation processes."),
                                topic("Employee Relations", "Handle workplace concerns, investigations, conflict resolution, and respectful communication."),
                                topic("HR Compliance", "Apply employment policies and maintain compliant documentation and reporting."),
                                topic("Workforce Planning", "Forecast staffing needs and align hiring, retention, and succession plans with business goals."))),
                new DepartmentDefinition(
                        "it",
                        "Information Technology (IT)",
                        "Handles software development, technical support, and system maintenance.",
                        List.of("Information Technology (IT)", "Information Technology", "IT"),
                        List.of(
                                topic("Software Development Lifecycle", "Understand requirements, design, implementation, testing, release, and maintenance stages."),
                                topic("Programming Fundamentals", "Apply core programming concepts, data structures, debugging, and readable coding practices."),
                                topic("Databases and SQL", "Model relational data and write safe queries for common retrieval and update tasks."),
                                topic("Web and API Development", "Build and integrate web services with HTTP, REST conventions, validation, and error handling."),
                                topic("Networking Fundamentals", "Understand network layers, addressing, DNS, routing, and common connectivity issues."),
                                topic("Cybersecurity Basics", "Recognize threats and apply authentication, access control, secure data handling, and incident reporting."),
                                topic("Cloud Computing", "Understand cloud service models, deployment patterns, identity, cost, and reliability basics."),
                                topic("DevOps and CI/CD", "Automate builds, tests, deployments, version control, and release monitoring."),
                                topic("IT Service Management", "Manage incidents, service requests, changes, assets, and service-level expectations."),
                                topic("System Troubleshooting", "Diagnose technical issues methodically, collect evidence, resolve root causes, and document fixes."))),
                new DepartmentDefinition(
                        "finance",
                        "Finance",
                        FINANCE_DESCRIPTION,
                        List.of("Finance"),
                        List.of(
                                topic("Budgeting and Forecasting", "Prepare budgets, monitor variances, and update forecasts using business assumptions."),
                                topic("Accounting Principles", "Apply accrual accounting, journal entries, reconciliations, and period-close fundamentals."),
                                topic("Payroll and Taxation", "Understand payroll calculations, statutory deductions, approvals, and secure payroll records."),
                                topic("Accounts Payable", "Validate invoices, manage approvals, schedule payments, and maintain vendor records."),
                                topic("Accounts Receivable", "Issue invoices, track collections, reconcile receipts, and manage overdue balances."),
                                topic("Financial Reporting", "Prepare accurate financial statements and interpret key financial indicators."),
                                topic("Cost Control", "Analyze cost drivers, identify savings, and assess the financial impact of decisions."),
                                topic("Internal Controls and Audit", "Apply segregation of duties, evidence requirements, reconciliations, and audit practices."),
                                topic("Cash Flow Management", "Monitor liquidity, payment timing, cash forecasts, and working capital needs."),
                                topic("Financial Compliance", "Follow financial policies, statutory reporting duties, and records retention requirements."))),
                new DepartmentDefinition(
                        "marketing",
                        "Marketing",
                        MARKETING_DESCRIPTION,
                        List.of("Marketing"),
                        List.of(
                                topic("Marketing Strategy", "Set measurable marketing objectives and align positioning, channels, and resources with business goals."),
                                topic("Brand Management", "Develop consistent brand positioning, identity, messaging, and brand governance."),
                                topic("Market Research", "Plan research, assess competitors, interpret market signals, and turn evidence into insights."),
                                topic("Customer Segmentation", "Group audiences by meaningful needs and behaviors and tailor value propositions."),
                                topic("Digital Marketing", "Plan digital channel activity across search, email, paid media, and owned platforms."),
                                topic("Content and SEO", "Create useful content, map it to audience intent, and improve organic discoverability."),
                                topic("Social Media Marketing", "Select social channels, create platform-appropriate content, and manage community engagement."),
                                topic("Campaign Planning and Analytics", "Set campaign goals, coordinate execution, track results, and optimize performance."),
                                topic("Product Marketing", "Translate product value into positioning, launch plans, enablement, and customer messaging."),
                                topic("Marketing Budget and ROI", "Allocate campaign budgets and evaluate return using consistent attribution assumptions."))),
                new DepartmentDefinition(
                        "sales",
                        "Sales",
                        "Manages customer relationships, sales activities, and revenue targets.",
                        List.of("Sales"),
                        List.of(
                                topic("Sales Process", "Manage sales stages from initial contact through discovery, proposal, negotiation, and close."),
                                topic("Prospecting and Lead Qualification", "Identify suitable prospects and qualify fit, need, authority, timing, and potential."),
                                topic("CRM and Pipeline Management", "Maintain accurate customer records, next steps, pipeline stages, and forecast data."),
                                topic("Product and Solution Knowledge", "Match product capabilities to customer needs and explain value accurately."),
                                topic("Consultative Selling", "Use discovery questions and active listening to understand business challenges."),
                                topic("Negotiation Skills", "Prepare negotiation objectives, communicate trade-offs, and build sustainable agreements."),
                                topic("Objection Handling", "Clarify customer concerns and respond with relevant evidence and options."),
                                topic("Account Management", "Strengthen customer relationships, monitor account health, and identify responsible growth."),
                                topic("Sales Forecasting", "Estimate revenue using pipeline evidence, historical trends, and clear assumptions."),
                                topic("Sales Ethics and Compliance", "Protect customer information, make accurate claims, and follow sales policies."))),
                new DepartmentDefinition(
                        "customer-support",
                        "Customer Support",
                        "Handles customer queries, complaints, and support requests.",
                        List.of("Customer Support", "Customer Service"),
                        List.of(
                                topic("Support Fundamentals", "Understand support responsibilities, service channels, ownership, and customer expectations."),
                                topic("Customer Communication", "Write clear, empathetic responses and adapt communication to customer needs."),
                                topic("Ticket Triage and Prioritization", "Categorize requests, assess urgency and impact, and route work to the right team."),
                                topic("Product Troubleshooting", "Gather symptoms, reproduce issues, test safe solutions, and document outcomes."),
                                topic("Complaint Handling and De-escalation", "Acknowledge concerns, manage difficult conversations, and agree on next steps."),
                                topic("Knowledge Base Management", "Create and maintain accurate help articles that support self-service and agents."),
                                topic("SLA and Escalation Management", "Track response commitments and escalate issues with useful context and evidence."),
                                topic("Customer Data Privacy", "Verify identity and protect personal and account information during support interactions."),
                                topic("Support Quality Assurance", "Evaluate interactions fairly and use feedback to improve accuracy and service."),
                                topic("Support Metrics and Improvement", "Interpret service measures and identify practical improvements to customer outcomes."))),
                new DepartmentDefinition(
                        "product-management",
                        "Product Management",
                        "Plans product features and coordinates product development.",
                        List.of("Product Management", "Product"),
                        List.of(
                                topic("Product Strategy", "Define target users, product outcomes, differentiation, and strategic measures of success."),
                                topic("Product Discovery and Research", "Validate user problems with interviews, observation, and evidence-based research."),
                                topic("User Stories and Requirements", "Translate user needs into clear requirements, acceptance criteria, and constraints."),
                                topic("Roadmapping", "Communicate product direction, sequencing, dependencies, and outcome-based milestones."),
                                topic("Prioritization Frameworks", "Compare opportunities using value, effort, risk, evidence, and strategic fit."),
                                topic("User Experience Fundamentals", "Apply usability, accessibility, interaction design, and user-centered principles."),
                                topic("Agile Product Delivery", "Collaborate with delivery teams through planning, refinement, reviews, and iteration."),
                                topic("Product Analytics", "Define product metrics and use behavioral data to evaluate outcomes and adoption."),
                                topic("Go-to-Market Planning", "Coordinate launch readiness, positioning, enablement, support, and adoption plans."),
                                topic("Stakeholder Management", "Align stakeholders through clear decisions, trade-offs, communication, and feedback."))),
                new DepartmentDefinition(
                        "training",
                        "Training and Development",
                        "Organizes employee training and skill development programs.",
                        List.of("Training and Development", "Training", "Learning and Development"),
                        List.of(
                                topic("Learning Needs Analysis", "Identify performance gaps and determine whether learning is the right intervention."),
                                topic("Learning Objectives", "Write measurable objectives that specify expected performance and evidence."),
                                topic("Instructional Design", "Structure learning experiences, practice, feedback, and assessment around outcomes."),
                                topic("Adult Learning Principles", "Design relevant, respectful, practice-oriented learning for adult learners."),
                                topic("Training Facilitation", "Deliver inclusive sessions, guide participation, and adapt to learner needs."),
                                topic("Digital Learning Design", "Create accessible online learning with clear navigation, interaction, and support."),
                                topic("Assessment and Evaluation", "Measure learning and transfer using suitable assessment and evaluation methods."),
                                topic("Coaching and Mentoring", "Support development through focused conversations, feedback, and agreed actions."),
                                topic("Career Development", "Connect employee interests, role capabilities, development opportunities, and career goals."),
                                topic("Learning Impact and Reporting", "Track participation, learning outcomes, transfer, and business impact responsibly."))),
                new DepartmentDefinition(
                        "administration",
                        "Administration",
                        "Manages office facilities, documentation, and administrative tasks.",
                        List.of("Administration", "Admin"),
                        List.of(
                                topic("Office Operations", "Coordinate daily office services, schedules, supplies, and workplace support."),
                                topic("Facilities Management", "Maintain safe, functional facilities and coordinate maintenance and access."),
                                topic("Document Management", "Create, organize, version, retrieve, and securely share business documents."),
                                topic("Records Retention", "Classify records and follow retention, archival, and secure disposal requirements."),
                                topic("Procurement Fundamentals", "Define purchasing needs, follow approval controls, and maintain purchasing evidence."),
                                topic("Vendor Management", "Coordinate vendor onboarding, service expectations, documentation, and issue resolution."),
                                topic("Business Communication", "Prepare accurate professional correspondence, meeting notes, and administrative notices."),
                                topic("Travel and Event Coordination", "Plan logistics, schedules, budgets, accessibility, and contingency arrangements."),
                                topic("Workplace Safety and Emergency Readiness", "Follow safety procedures and support emergency planning and incident reporting."),
                                topic("Administrative Compliance", "Apply office policies, privacy practices, approval workflows, and audit-ready recordkeeping."))));
    }

    private static TopicSeed topic(String title, String outline) {
        return new TopicSeed(title, outline);
    }

    private record TopicKey(Long departmentId, int topicOrder) {
    }

    private record SlotKey(Long topicId, int slotNumber) {
    }

    private record LevelKey(Long slotId, QuizDifficulty difficulty) {
    }

    private record TopicSeed(String title, String outline) {
    }

    private record DepartmentDefinition(
            String key,
            String name,
            String description,
            List<String> aliases,
            List<TopicSeed> topics) {
    }
}
