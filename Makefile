SBT = sbt

VCS = vcs -full64 -sverilog -timescale=1ns/1ns 	+v2k -debug_access+all -kdb -lca -f hdl/filelist.f tb/tb.sv

# Generate Verilog code
run:
	sbt "runMain adder.Main"
	@sed -i 's/\/\/ @.*//g' hdl/*.v
	@find hdl/ -type f -name "*.v" > hdl/filelist.f

.PHONY: clean test wave comp verdi sim vlt vlt_wave
clean:
	@rm -rf verdiLog
	@rm -rf project
	@rm -rf obj_dir
	@rm -rf logs
	@rm -rf hdl
	@rm -rf generated
	@rm -rf csrc
	@rm -rf target
	@rm -rf simv.daidir
	@rm -f simv ucli.key novas_dump.log *.fsdb *.vcd *.sv *.v novas.* 
	@rm -rf test_run_dir
	@rm -f *.fir *_obj *.anno.json *.log

test:
	$(SBT) "testOnly adder.SimpleTest" --color=always 2>&1 | tee test.log

wave:
	gtkwave "test_run_dir/DUT_should_pass/Adder.vcd"

comp:
	$(VCS) 

verdi:
	verdi -ssf rtl.fsdb -nologo
 
sim:
	./simv 2>&1 | tee sim.log
	@if grep -q "failed " sim.log; then \
        echo -e "\033[0;31m********************************Fail********************************"; \
    else \
        echo -e "\033[0;32m********************************Pass********************************"; \
    fi
	
vlt:
	verilator -cc --exe -x-assign fast -Wall --trace --assert --coverage -f input.vc -sv verilator_cpp/top.sv  hdl/Cmm.v hdl/HdmDecoder.v hdl/SyncSRam.v hdl/CircleFifo.v  verilator_cpp/sim_main.cpp
	make -j -C obj_dir -f ../Makefile_obj
	@echo "------------- RUN Verilagot Sim  -------------------"
	@rm -rf logs
	@mkdir -p logs
	obj_dir/Vtop +trace

vlt_wave:
	gtkwave logs/vlt_dump.vcd