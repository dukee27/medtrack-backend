package com.halt.medtracker.medication_tracker_api.service;

import com.halt.medtracker.medication_tracker_api.constants.ActivityActionType;
import com.halt.medtracker.medication_tracker_api.constants.ActivityEntityType;
import com.halt.medtracker.medication_tracker_api.constants.OrganizationType;
import com.halt.medtracker.medication_tracker_api.domain.identity.User;
import com.halt.medtracker.medication_tracker_api.domain.organization.Organization;
import com.halt.medtracker.medication_tracker_api.domain.organization.OrganizationMember;
import com.halt.medtracker.medication_tracker_api.dto.request.AddMemberRequest;
import com.halt.medtracker.medication_tracker_api.dto.request.CreateOrganizationRequest;
import com.halt.medtracker.medication_tracker_api.dto.response.OrganizationMemberResponseDTO;
import com.halt.medtracker.medication_tracker_api.dto.response.OrganizationResponseDTO;
import com.halt.medtracker.medication_tracker_api.exception.ResourceNotFoundException;
import com.halt.medtracker.medication_tracker_api.repository.OrganizationMemberRepository;
import com.halt.medtracker.medication_tracker_api.repository.OrganizationRepository;
import com.halt.medtracker.medication_tracker_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final OrganizationMemberRepository memberRepository;
    private final UserRepository userRepository;
    private final ActivityLogService activityLogService;

    @Transactional
    public OrganizationResponseDTO createOrganizationDTO(User owner, CreateOrganizationRequest request) {
        Organization org = Organization.builder()
                .name(request.getName())
                .type(request.getType())
                .owner(owner)
                .build();
        Organization savedOrg = organizationRepository.save(org);

        // Auto-add owner as ADMIN
        OrganizationMember adminMember = OrganizationMember.builder()
                .organization(savedOrg)
                .user(owner)
                .role("ADMIN")
                .approved(true)
                .build();
        memberRepository.save(adminMember);

        activityLogService.logActivity(owner, owner, ActivityEntityType.ORGANIZATION.name(),
            ActivityActionType.CREATE.name(),
            "{\"orgId\": " + savedOrg.getId() + "}",
            owner.getFirstName() + " created a new " + request.getType() + " bucket: " + request.getName());

        return toOrganizationDTO(savedOrg, 1);
    }

    @Transactional(readOnly = true)
    public List<OrganizationResponseDTO> getMyOrganizationsDTO(User user) {
        List<OrganizationMember> memberships = memberRepository.findByUserAndIsDeletedFalse(user);
        return memberships.stream()
            .filter(m -> !m.getOrganization().isDeleted())
            .map(m -> {
                Organization org = m.getOrganization();
                int memberCount = memberRepository.findByOrganizationAndIsDeletedFalse(org).size();
                return toOrganizationDTO(org, memberCount);
            })
            .distinct()
            .toList();
    }

    @Transactional(readOnly = true)
    public List<OrganizationMemberResponseDTO> getOrganizationMembersDTO(User actor, Long orgId) {
        Organization org = organizationRepository.findByIdAndIsDeletedFalse(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));
        memberRepository.findByOrganizationAndUserAndIsDeletedFalse(org, actor)
                .orElseThrow(() -> new SecurityException("You do not have access to this organization"));
        return memberRepository.findByOrganizationAndIsDeletedFalse(org)
                .stream().map(m -> toMemberDTO(m, org)).toList();
    }

    @Transactional
    public OrganizationMemberResponseDTO addMemberDTO(User actor, Long orgId, AddMemberRequest request) {
        Organization org = organizationRepository.findByIdAndIsDeletedFalse(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));

        if (!org.getOwner().getId().equals(actor.getId())) {
            throw new SecurityException("Only bucket owners can add members");
        }

        User targetUser = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User with email not found: " + request.getEmail()));

        if (memberRepository.findByOrganizationAndUserAndIsDeletedFalse(org, targetUser).isPresent()) {
            throw new IllegalArgumentException("User is already a member of this bucket");
        }

        boolean isApproved = org.getType() == OrganizationType.HOME;

        OrganizationMember member = OrganizationMember.builder()
                .organization(org)
                .user(targetUser)
                .role(request.getRole())
                .approved(isApproved)
                .build();
        OrganizationMember saved = memberRepository.save(member);

        activityLogService.logActivity(actor, targetUser, ActivityEntityType.ACCESS_CONTROL.name(),
            ActivityActionType.CREATE.name(),
            "{\"orgId\": " + orgId + ", \"role\": \"" + request.getRole() + "\"}",
            actor.getFirstName() + " added you to " + org.getName());

        return toMemberDTO(saved, org);
    }

    @Transactional
    public void removeMember(User actor, Long orgId, Long memberId, String reason) {
        Organization org = organizationRepository.findByIdAndIsDeletedFalse(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));
        if (!org.getOwner().getId().equals(actor.getId())) {
            throw new SecurityException("Only bucket owners can remove members");
        }
        OrganizationMember member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Membership not found"));

        member.setDeleted(true);
        member.setDeletedAt(LocalDateTime.now());
        member.setDeletedBy(actor.getEmail());
        member.setDeleteReason(reason != null ? reason : "Removed by owner");
        memberRepository.save(member);

        activityLogService.logActivity(actor, member.getUser(), ActivityEntityType.ACCESS_CONTROL.name(),
            ActivityActionType.DELETE.name(),
            "{\"orgId\": " + orgId + "}",
            actor.getFirstName() + " removed you from " + org.getName());
    }

    // Raw versions for backward compat
    @Transactional
    public Organization createOrganization(User owner, CreateOrganizationRequest request) {
        Organization org = Organization.builder()
                .name(request.getName()).type(request.getType()).owner(owner).build();
        Organization savedOrg = organizationRepository.save(org);
        OrganizationMember adminMember = OrganizationMember.builder()
                .organization(savedOrg).user(owner).role("ADMIN").approved(true).build();
        memberRepository.save(adminMember);
        return savedOrg;
    }

    @Transactional(readOnly = true)
    public List<Organization> getMyOrganizations(User user) {
        return memberRepository.findByUserAndIsDeletedFalse(user).stream()
            .map(OrganizationMember::getOrganization).filter(org -> !org.isDeleted()).toList();
    }

    @Transactional(readOnly = true)
    public List<OrganizationMember> getOrganizationMembers(User actor, Long orgId) {
        Organization org = organizationRepository.findByIdAndIsDeletedFalse(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));
        memberRepository.findByOrganizationAndUserAndIsDeletedFalse(org, actor)
                .orElseThrow(() -> new SecurityException("You do not have access to this organization"));
        return memberRepository.findByOrganizationAndIsDeletedFalse(org);
    }

    @Transactional
    public OrganizationMember addMember(User actor, Long orgId, AddMemberRequest request) {
        Organization org = organizationRepository.findByIdAndIsDeletedFalse(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));
        if (!org.getOwner().getId().equals(actor.getId())) {
            throw new SecurityException("Only bucket owners can add members directly");
        }
        User targetUser = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (memberRepository.findByOrganizationAndUserAndIsDeletedFalse(org, targetUser).isPresent()) {
            throw new IllegalArgumentException("User is already a member");
        }
        boolean isApproved = org.getType() == OrganizationType.HOME;
        OrganizationMember member = OrganizationMember.builder()
                .organization(org).user(targetUser).role(request.getRole()).approved(isApproved).build();
        return memberRepository.save(member);
    }

    // DTO helpers
    private OrganizationResponseDTO toOrganizationDTO(Organization org, int memberCount) {
        return OrganizationResponseDTO.builder()
                .id(org.getId())
                .name(org.getName())
                .type(org.getType())
                .ownerId(org.getOwner().getId())
                .ownerEmail(org.getOwner().getEmail())
                .ownerFirstName(org.getOwner().getFirstName())
                .ownerLastName(org.getOwner().getLastName())
                .memberCount(memberCount)
                .createdAt(org.getCreatedAt())
                .build();
    }

    private OrganizationMemberResponseDTO toMemberDTO(OrganizationMember member, Organization org) {
        return OrganizationMemberResponseDTO.builder()
                .id(member.getId())
                .organizationId(org.getId())
                .organizationName(org.getName())
                .userId(member.getUser().getId())
                .userEmail(member.getUser().getEmail())
                .userFirstName(member.getUser().getFirstName())
                .userLastName(member.getUser().getLastName())
                .role(member.getRole())
                .approved(member.isApproved())
                .createdAt(member.getCreatedAt())
                .build();
    }
}
