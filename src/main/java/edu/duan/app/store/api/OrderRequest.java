package edu.duan.app.store.api;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    private String login;
    private int itemId;
    private int count;
}
