package com.ureca.picky_be.base.persistence.board;

import com.ureca.picky_be.base.business.board.dto.contentDto.BoardContentWithBoardId;
import com.ureca.picky_be.jpa.entity.board.BoardContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
public interface BoardContentRepository extends JpaRepository<BoardContent, Long> {

    @Transactional
    List<BoardContent> findByBoardId(Long boardId);


    @Modifying
    @Transactional
    @Query("DELETE FROM BoardContent bc WHERE bc.board.id = :boardId")
    void deleteByBoardId(Long boardId);

    @Query("""
    SELECT new com.ureca.picky_be.base.business.board.dto.contentDto.BoardContentWithBoardId(
        bc.board.id,
        bc.contentUrl,
        bc.boardContentType
    )
    FROM BoardContent bc
    WHERE bc.board.id IN :boardIds
""")
    List<BoardContentWithBoardId> findByBoardIds(@Param("boardIds") List<Long> boardIds);
}
