<?xml version="1.0" encoding="UTF-8"?>
<!-- origin at X=0.0 Y=0.0 -->
<bpmn2:definitions xmlns:tl="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:bpmn2="http://www.omg.org/spec/BPMN/20100524/MODEL" xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI" xmlns:dc="http://www.omg.org/spec/DD/20100524/DC" xmlns:di="http://www.omg.org/spec/DD/20100524/DI" id="Definitions_1" exporter="org.eclipse.bpmn2.modeler.core" exporterVersion="1.3.2.Final-v20161020-1541-B59" xmlns:types="%typesnamespace%" targetNamespace="%targetnamespace%">
  <bpmn2:import importType="http://www.w3.org/2001/XMLSchema" location="types.xsd" namespace="%typesnamespace%"/>
  <bpmn2:choreography id="Choreography_1" name="Default Choreography">
    <bpmn2:participant id="Participant_1" name="Initiating Participant"/>
    <bpmn2:participant id="Participant_2" name="Non-initiating Participant"/>
    <bpmn2:choreographyTask id="ChoreographyTask_1" name="Choreography Task" initiatingParticipantRef="Participant_1">
      <bpmn2:incoming>SequenceFlow_1</bpmn2:incoming>
      <bpmn2:outgoing>SequenceFlow_2</bpmn2:outgoing>
      <bpmn2:participantRef>Participant_1</bpmn2:participantRef>
      <bpmn2:participantRef>Participant_2</bpmn2:participantRef>
    </bpmn2:choreographyTask>
    <bpmn2:startEvent id="StartEvent_1" name="Start Event 1">
      <bpmn2:outgoing>SequenceFlow_1</bpmn2:outgoing>
    </bpmn2:startEvent>
    <bpmn2:endEvent id="EndEvent_1" name="End Event 1">
      <bpmn2:incoming>SequenceFlow_2</bpmn2:incoming>
    </bpmn2:endEvent>
    <bpmn2:sequenceFlow id="SequenceFlow_1" sourceRef="StartEvent_1" targetRef="ChoreographyTask_1"/>
    <bpmn2:sequenceFlow id="SequenceFlow_2" sourceRef="ChoreographyTask_1" targetRef="EndEvent_1"/>
  </bpmn2:choreography>
  <bpmndi:BPMNDiagram id="BPMNDiagram_1">
    <bpmndi:BPMNPlane id="BPMNPlane_1" bpmnElement="Choreography_1">
      <bpmndi:BPMNShape id="BPMNShape_1" bpmnElement="ChoreographyTask_1">
        <dc:Bounds height="150.0" width="150.0" x="180.0" y="100.0"/>
        <bpmndi:BPMNLabel id="BPMNLabel_1">
          <dc:Bounds height="17.0" width="128.0" x="191.0" y="166.0"/>
        </bpmndi:BPMNLabel>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="BPMNShape_2" bpmnElement="Participant_1" choreographyActivityShape="BPMNShape_1" isHorizontal="true">
        <dc:Bounds height="21.0" width="150.0" x="100.0" y="100.0"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="BPMNShape_3" bpmnElement="Participant_2" choreographyActivityShape="BPMNShape_1" isHorizontal="true" participantBandKind="bottom_non_initiating">
        <dc:Bounds height="21.0" width="150.0" x="100.0" y="229.0"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="BPMNShape_StartEvent_1" bpmnElement="StartEvent_1">
        <dc:Bounds height="36.0" width="36.0" x="59.0" y="157.0"/>
        <bpmndi:BPMNLabel id="BPMNLabel_2">
          <dc:Bounds height="34.0" width="80.0" x="37.0" y="193.0"/>
        </bpmndi:BPMNLabel>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="BPMNShape_Participant_5" bpmnElement="Participant_1" choreographyActivityShape="BPMNShape_1" isHorizontal="true">
        <dc:Bounds height="20.0" width="150.0" x="180.0" y="100.0"/>
        <bpmndi:BPMNLabel id="BPMNLabel_3">
          <dc:Bounds height="17.0" width="125.0" x="192.0" y="101.0"/>
        </bpmndi:BPMNLabel>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="BPMNShape_Participant_6" bpmnElement="Participant_2" choreographyActivityShape="BPMNShape_1" isHorizontal="true" participantBandKind="bottom_non_initiating">
        <dc:Bounds height="20.0" width="150.0" x="180.0" y="230.0"/>
        <bpmndi:BPMNLabel id="BPMNLabel_4">
          <dc:Bounds height="34.0" width="90.0" x="210.0" y="223.0"/>
        </bpmndi:BPMNLabel>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="BPMNShape_EndEvent_1" bpmnElement="EndEvent_1">
        <dc:Bounds height="36.0" width="36.0" x="403.0" y="157.0"/>
        <bpmndi:BPMNLabel id="BPMNLabel_5">
          <dc:Bounds height="17.0" width="79.0" x="382.0" y="193.0"/>
        </bpmndi:BPMNLabel>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNEdge id="BPMNEdge_SequenceFlow_1" bpmnElement="SequenceFlow_1" sourceElement="BPMNShape_StartEvent_1" targetElement="BPMNShape_1">
        <di:waypoint xsi:type="dc:Point" x="95.0" y="175.0"/>
        <di:waypoint xsi:type="dc:Point" x="137.0" y="175.0"/>
        <di:waypoint xsi:type="dc:Point" x="180.0" y="175.0"/>
        <bpmndi:BPMNLabel id="BPMNLabel_6"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="BPMNEdge_SequenceFlow_2" bpmnElement="SequenceFlow_2" sourceElement="BPMNShape_1" targetElement="BPMNShape_EndEvent_1">
        <di:waypoint xsi:type="dc:Point" x="330.0" y="175.0"/>
        <di:waypoint xsi:type="dc:Point" x="366.0" y="175.0"/>
        <di:waypoint xsi:type="dc:Point" x="403.0" y="175.0"/>
        <bpmndi:BPMNLabel id="BPMNLabel_7"/>
      </bpmndi:BPMNEdge>
    </bpmndi:BPMNPlane>
  </bpmndi:BPMNDiagram>
</bpmn2:definitions>