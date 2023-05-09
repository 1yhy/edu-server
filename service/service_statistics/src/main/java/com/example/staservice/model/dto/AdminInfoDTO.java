package com.example.staservice.model.dto;

import com.example.commonutils.model.dto.CategoryDTO;
import com.example.commonutils.model.dto.LessonViewDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author 31913
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminInfoDTO {

  private Integer viewsCount;

  private Integer orderCount;

  private Integer userCount;

  private Integer lessonCount;

  private List<CategoryDTO> categoryDTOs;

  private List<LessonViewDTO> lessonViewDTOs;

  private List<UniqueViewDTO> uniqueViewDTOs;
}

