import { styled } from "styled-components";

import {
  DIRECTORY_NAME_HOLDER,
  DIRECTORY_NAME_LABEL,
} from "../../data/constants";
import Buttons from "./Buttons";

const EditBox = ({ EditPage, category, changeCategory, postCategory }) => {
  return (
    <>
      <Wrapper>
        <Label for="directoryName">{DIRECTORY_NAME_LABEL}</Label>
        <Input
          id="directoryName"
          value={category}
          placeholder={DIRECTORY_NAME_HOLDER}
          onChange={(event) => changeCategory(event.target.value)}
        ></Input>
        <Buttons EditPage={EditPage} postCategory={postCategory} />
      </Wrapper>
    </>
  );
};

export default EditBox;

const Wrapper = styled.div`
  width: 100%;

  padding: 40px 15px 10px 15px;
  border: 1px solid #d0d0d0;
  border-radius: 15px;

  display: flex;
  flex-direction: column;
`;

const Label = styled.label`
  margin: 5px 10px;
`;

const Input = styled.input`
  width: 100%;
  height: 40px;

  padding: 15px;
  border: 1px solid #d0d0d0;
  border-radius: 15px;
`;
