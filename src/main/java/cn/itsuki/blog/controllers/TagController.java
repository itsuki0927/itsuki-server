package cn.itsuki.blog.controllers;

import cn.itsuki.blog.entities.Tag;
import cn.itsuki.blog.entities.requests.SearchTagInput;
import cn.itsuki.blog.entities.requests.TagActionInput;
import cn.itsuki.blog.entities.responses.SearchResponse;
import cn.itsuki.blog.services.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;

@Controller
public class TagController {
    @Autowired
    private TagService tagService;

    @QueryMapping
    public SearchResponse<Tag> tags(@Argument SearchTagInput input) {
        return this.tagService.tags(input);
    }

    @Secured("ROLE_ADMIN")
    @QueryMapping
    public int syncAllTagCount() {
        return this.tagService.syncAllTagCount();
    }

    @Secured("ROLE_ADMIN")
    @MutationMapping
    public Tag createTag(@Argument TagActionInput input) {
        return this.tagService.createTag(input);
    }

    @Secured("ROLE_ADMIN")
    @MutationMapping
    public Tag updateTag(@Argument Long id, @Argument TagActionInput input) {
        return this.tagService.updateTag(id, input);
    }

    @Secured("ROLE_ADMIN")
    @MutationMapping
    public int deleteTag(@Argument Long id) {
        return this.tagService.deleteTag(id);
    }
}
