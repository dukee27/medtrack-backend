package com.halt.medtracker.medication_tracker_api.repository;

import com.halt.medtracker.medication_tracker_api.domain.identity.User;
import com.halt.medtracker.medication_tracker_api.domain.organization.Organization;
import com.halt.medtracker.medication_tracker_api.domain.organization.OrganizationMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrganizationMemberRepository extends JpaRepository<OrganizationMember, Long> {
    List<OrganizationMember> findByUserAndIsDeletedFalse(User user);
    List<OrganizationMember> findByOrganizationAndIsDeletedFalse(Organization organization);
    Optional<OrganizationMember> findByOrganizationAndUserAndIsDeletedFalse(Organization organization, User user);
}