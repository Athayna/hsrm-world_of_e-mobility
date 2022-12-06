package de.hsrm.mi.swt_project.demo;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import de.hsrm.mi.swt_project.demo.instancehandling.EditorInstance;
import de.hsrm.mi.swt_project.demo.instancehandling.InstanceHandler;

@SpringBootTest
@AutoConfigureMockMvc
class EditorTest {
    
    @Autowired 
    private MockMvc mockMvc;

    @Autowired
    private InstanceHandler instanceHandler;

    EditorInstance editorInstance;

    @BeforeEach
    void setUp() {
        instanceHandler.createEditorInstance("");
        
    }

    @Test
    void post_map_update_good() throws Exception {

        JSONObject body = new JSONObject();
        body.put("mapName", "Test");
        body.put("mapId", "0");
        body.put("control", "PLACE");
        body.put("type", "STREET_STRAIGHT");


        mockMvc.perform(
            post("/api/editor/mapupdate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(body.toString())
            ).andExpect(status().isOk());


    }

    @Test
    void post_instancelist_good() throws Exception {
        int amountEditorItems = instanceHandler.getEditorInstances().size();

        mockMvc.perform(
            post("/api/editor/instancelist")
        ).andExpect(status().isOk())
        .andExpect(jsonPath("$.instancelist", hasSize(amountEditorItems)));

    }


}
