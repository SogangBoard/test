package dev.board.boardprojectproto.model

import javax.persistence.*


// 게시판 상관없이 모두 적용되는 속성을 넣는다.
// 테이블 전략은 상속관계 매핑 중에서 조인 전략을 사용

// 유저는 아직
// 좋아요, 댓글 개수는 count query 사용해서 한다.

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn
open class BasePost(
    var title: String,
    var content: String,
    //val authorId : Long,
    var isAnon: Boolean,
    var commentOn: Boolean,
    // var showStatus: 이거는 enum 만들기
    @Id
    @Column(name = "post_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private var id: Long? = null,
) : BaseEntity()
