import { styled } from "styled-components";

import Title from "./Title";
import EditBox from "./EditBox";

const DirectoryEditBox = () => {
  return (
    <>
      <EditSection>
        <Title />
        <EditBox />
      </EditSection>
    </>
  );
};

export default DirectoryEditBox;

const EditSection = styled.section`
  width: 100%;

  padding: 0px 35px;
`;
