package com.in.service;

import com.in.model.Interview;
import com.in.repository.InterviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InterviewService {

    private final InterviewRepository interviewRepository;

    // Admin interview create karega
    public Interview createInterview(Interview interview) {
        return interviewRepository.save(interview);
    }

    // Pagination + Search + Sorting support
    public Page<Interview> getAllInterviews(String keyword,
                                            int page,
                                            int size,
                                            String sortBy,
                                            String direction) {

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        // Agar keyword empty hai to normal pagination
        if (keyword == null || keyword.isBlank()) {
            return interviewRepository.findAll(pageable);
        }

        // Search by title OR company
        return interviewRepository
                .findByTitleContainingIgnoreCaseOrCompanyContainingIgnoreCase(
                        keyword,
                        keyword,
                        pageable
                );
    }
}