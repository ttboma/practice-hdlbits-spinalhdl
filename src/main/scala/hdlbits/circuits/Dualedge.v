module HdlBitsDualedgeFlipFlop (
    input clk,
    input d,
    output q
);
    reg [1:0] q_dly;
    always @( posedge clk ) begin
        q_dly[0] <= d ^ q_dly[1];
    end
    always @( negedge clk ) begin
        q_dly[1] <= d ^ q_dly[0];
    end
    assign q = q_dly[0] ^ q_dly[1];
endmodule
