module HdlBitsDLatch (
    input wire D,
    input wire enable,
    output wire Q
);
    reg Q_reg;

    always @* begin
        if (enable) begin
            Q_reg = D;
        end
    end

    assign Q = Q_reg;
endmodule
